package br.com.santanna.ponto_eletronico.domain.service.impl

import br.com.santanna.ponto_eletronico.app.handler.model.DataIntegrityViolationException
import br.com.santanna.ponto_eletronico.app.handler.model.ObjectNotFoundException
import br.com.santanna.ponto_eletronico.domain.dataprovider.EmployeeDataProvider
import br.com.santanna.ponto_eletronico.domain.dataprovider.TimeRecordDataProvider
import br.com.santanna.ponto_eletronico.domain.dto.timeRecord.*
import br.com.santanna.ponto_eletronico.domain.entity.Employee
import br.com.santanna.ponto_eletronico.domain.entity.TimeRecord
import br.com.santanna.ponto_eletronico.domain.service.TimeRecordService
import br.com.santanna.ponto_eletronico.infrastructure.security.configsec.JwtTokenUtil
import br.com.santanna.ponto_eletronico.infrastructure.util.DateUtils
import jakarta.transaction.Transactional
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.abs
import kotlin.reflect.KMutableProperty1


private const val CHECKIN_EXCEPTION = "Necessário realizar o checkout para o checkin em aberto"
private const val CHECKOUT_EXCEPTION = "Não há checkin aberto para ser encerrado"
private const val ZONE_TIME = "America/Sao_Paulo"



@Service
data class TimeRecordServiceImpl(
    private val timeRecordDataProvider: TimeRecordDataProvider, private val employeeDataProvider: EmployeeDataProvider,
    private val mapper: ModelMapper, private val jwtTokenUtil: JwtTokenUtil
): TimeRecordService {

    @Transactional
    override fun registerCheckin(): RecordCheckinDto? {
        val id = getCurrentUserId()
        val employee = employeeDataProvider.findById(id)

        findLastTimeRecord(employee)?.let {
            throw Exception(CHECKIN_EXCEPTION)
        }

        val newRegister = TimeRecord().apply {
            this.employee = employee
            this.startWorkTime = LocalDateTime.now(ZoneId.of(ZONE_TIME))
        }

        val savedCheckin = timeRecordDataProvider.save(newRegister)
        return createRecordCheckinDto(savedCheckin)
    }

    @Transactional
    override fun registerCheckout(): RecordCheckoutDto? {
        val id = getCurrentUserId()
        val employee = employeeDataProvider.findById(id)

        val lastRecord = findLastTimeRecord(employee) ?: throw Exception(CHECKOUT_EXCEPTION)
        val currentDateTimeInBrasilia = LocalDateTime.now(ZoneId.of(ZONE_TIME))

        lastRecord.endWorkTime = currentDateTimeInBrasilia
        lastRecord.timeWorked = Duration.between(lastRecord.startWorkTime, currentDateTimeInBrasilia).toMinutes()

        val savedCheckout = timeRecordDataProvider.save(lastRecord)
        return createRecordCheckoutDto(savedCheckout)
    }

    @Transactional
    override fun updateTimeRecord(updateTimeRecordRequestDto: UpdateTimeRecordRequestDto): UpdateTimeRecordDto {
        validateEmployeeAndPassword(
            updateTimeRecordRequestDto.employeeCpfTarget,
            updateTimeRecordRequestDto.employeeManagerCpf,
            updateTimeRecordRequestDto.passwords
        )

        val timeRecord = timeRecordDataProvider.findById(updateTimeRecordRequestDto.timeRecordId)
            ?: throw ObjectNotFoundException("Time record not found with ID: ${updateTimeRecordRequestDto.timeRecordId}")

        updateTimeRecordRequestDto.updateTimeRecordDto.let {
            updateRecordFields(timeRecord, it)
        }

        val savedUpdate = timeRecordDataProvider.save(timeRecord)
        return convertToUpdateTimeRecordDto(savedUpdate)
    }

    override fun balanceHoursByDateForManager(cpf: String, startDate: LocalDate, endDate: LocalDate): BalanceHoursDto {
        val timeRecords = findTimeRecordsByDateRange(cpf, startDate, endDate)
        val recordsByDate = timeRecords.groupBy { it.startWorkTime?.toLocalDate() }

        var totalWorkedMinutes = 0L
        var totalExpectedMinutes = 0L

        for ((_, records) in recordsByDate) {
            totalWorkedMinutes += records.sumOf { it.timeWorked ?: 0 }
            totalExpectedMinutes += 8 * 60
        }

        val balance = totalWorkedMinutes - totalExpectedMinutes
        val formattedBalance = formatBalance(balance)

        return BalanceHoursDto(employeeCpf = cpf, balance = formattedBalance)
    }

    override fun getTimeRecordsByEmployeeCpfAndDateRangePageableForManager(cpf: String, startDate: LocalDate, endDate: LocalDate, pageable: Pageable): Page<DetailedTimeRecordDto> {
        val timeRecords = timeRecordDataProvider.findByEmployeeCpfAndDateRange(
            cpf, startDate.atStartOfDay(), endDate.atTime(23, 59, 59), pageable
        )
        return timeRecords.map { convertToDetailedTimeRecordDto(it) }
    }

    override fun balanceHoursByDate(startDate: LocalDate, endDate: LocalDate): BalanceHoursDto {
        val id = getCurrentUserId()
        val employee = employeeDataProvider.findById(id)

        val timeRecords = findTimeRecordsByDateRange(employee.cpf!!, startDate, endDate)
        val recordsByDate = timeRecords.groupBy { it.startWorkTime?.toLocalDate() }

        var totalWorkedMinutes = 0L
        var totalExpectedMinutes = 0L

        for ((_, records) in recordsByDate) {
            val workedMinutesPerDay = records.sumOf { it.timeWorked ?: 0 }
            totalWorkedMinutes += workedMinutesPerDay
            totalExpectedMinutes += 8 * 60
        }

        val balance = totalWorkedMinutes - totalExpectedMinutes

        val balanceHours = abs(balance / 60)
        val balanceRemainingMinutes = abs(balance % 60)

        val sign = if (balance < 0) "-" else ""

        val formattedBalance = String.format("%s%02d:%02d", sign, balanceHours, balanceRemainingMinutes)

        return BalanceHoursDto(
            employeeCpf = employee.cpf!!,
            balance = formattedBalance
        )
    }

    override fun getTimeRecordsByEmployeeCpfAndDateRangePageable(
        startDate: LocalDate,
        endDate: LocalDate,
        pageable: Pageable
    ): Page<DetailedTimeRecordDto> {
        val id = getCurrentUserId()
        val employee = employeeDataProvider.findById(id)

        val startDateTime = startDate.atStartOfDay()
        val endDateTime = endDate.atTime(23, 59, 59)
        val timeRecords = timeRecordDataProvider.findByEmployeeCpfAndDateRange(employee.cpf!!, startDateTime, endDateTime, pageable)
        return timeRecords.map { convertToDetailedTimeRecordDto(it) }
    }
    @Transactional
    override fun deleteTimeRecord(deleteTimeRecordRequestDto: DeleteTimeRecordRequestDto) {
        validateEmployeeAndPassword(
            deleteTimeRecordRequestDto.employeeCpfTarget,
            deleteTimeRecordRequestDto.employeeManagerCpf,
            deleteTimeRecordRequestDto.passwords
        )

        val timeRecord = timeRecordDataProvider.findById(deleteTimeRecordRequestDto.timeRecordId)
            ?: throw ObjectNotFoundException("Time record not found with ID: ${deleteTimeRecordRequestDto.timeRecordId}")

        if (timeRecord.employee?.cpf != deleteTimeRecordRequestDto.employeeCpfTarget) {
            throw DataIntegrityViolationException("Registro não pertence ao cpf: ${deleteTimeRecordRequestDto.employeeCpfTarget} informado")
        }

        timeRecordDataProvider.delete(timeRecord)
    }

    private fun validateEmployeeAndPassword(employeeCpfTarget: String, employeeManagerCpf: String, password: String) {
        findEmployeeByCpfOrThrow(employeeCpfTarget)
        val manager = findEmployeeByCpfOrThrow(employeeManagerCpf)

        if (!BCryptPasswordEncoder().matches(password, manager.password)) {
            throw IllegalArgumentException("Invalid password")
        }
    }

    private fun findEmployeeByCpfOrThrow(cpf: String): Employee {
        return employeeDataProvider.findCpf(cpf) ?: throw ObjectNotFoundException("Employee not found with CPF: $cpf")
    }

    private fun findTimeRecordsByDateRange(cpf: String, startDate: LocalDate, endDate: LocalDate): List<TimeRecord> {
        val startDateTime = startDate.atStartOfDay()
        val endDateTime = endDate.atTime(23, 59, 59)
        return timeRecordDataProvider.findByEmployeeCpfAndDateRange(cpf, startDateTime, endDateTime)
    }

    private fun findLastTimeRecord(employee: Employee?): TimeRecord? {
        return timeRecordDataProvider.findTopByEmployeeAndEndWorkTimeIsNullOrderByStartWorkTimeDesc(employee)
    }

    private fun createRecordCheckinDto(savedCheckin: TimeRecord): RecordCheckinDto {
        return RecordCheckinDto(
            id = savedCheckin.id,
            startOfWorkTime = DateUtils.formatTime(savedCheckin.startWorkTime),
            startOfWorkDate = DateUtils.formatDate(savedCheckin.startWorkTime?.toLocalDate())
        )
    }

    private fun createRecordCheckoutDto(savedCheckout: TimeRecord): RecordCheckoutDto {
        return RecordCheckoutDto(
            id = savedCheckout.id,
            endWorkTime = DateUtils.formatTime(savedCheckout.endWorkTime),
            timeWorked = formatTimeWorked(savedCheckout.timeWorked),
            endWorkDate = DateUtils.formatDate(savedCheckout.endWorkTime?.toLocalDate())
        )
    }

    private fun updateRecordFields(timeRecord: TimeRecord, updateTimeRecordDto: UpdateTimeRecordDto) {
        updateTimeRecordField(timeRecord, updateTimeRecordDto.startWorkDate, updateTimeRecordDto.startWorkTime, TimeRecord::startWorkTime, "start")
        updateTimeRecordField(timeRecord, updateTimeRecordDto.endWorkDate, updateTimeRecordDto.endWorkTime, TimeRecord::endWorkTime, "end")
    }

    private fun updateTimeRecordField(
        timeRecord: TimeRecord,
        newDate: String?,
        newTime: String?,
        dateTimeField: KMutableProperty1<TimeRecord, LocalDateTime?>,
        dateType: String
    ) {
        if (newDate != null && newTime != null) {
            val newDateTime = DateUtils.parseDateTime(newDate, newTime)
            validateDateChange(dateTimeField.get(timeRecord), newDateTime, dateType)
            dateTimeField.set(timeRecord, newDateTime)

            if (dateTimeField == TimeRecord::endWorkTime) {
                val duration = Duration.between(timeRecord.startWorkTime, newDateTime)
                timeRecord.timeWorked = duration.toMinutes()
            }
        }
    }

    private fun validateDateChange(currentDateTime: LocalDateTime?, newDateTime: LocalDateTime, dateType: String) {
        if (currentDateTime == null) {
            throw DataIntegrityViolationException("$dateType Data nao pode ser vazia ")
        }

        if (currentDateTime.year != newDateTime.year) {
            throw DataIntegrityViolationException("Ano nao pode ser alterado, deve permanecer o ano vigente")
        }

        if (currentDateTime.month != newDateTime.month) {
            throw DataIntegrityViolationException("O mês deve ser o mesmo do registro que será alterado")
        }

        if (newDateTime.dayOfMonth > currentDateTime.dayOfMonth) {
            throw DataIntegrityViolationException("O dia deve ser igual ou anterior ao do registro atual")
        }
    }

    private fun formatBalance(balance: Long): String {
        val sign = if (balance < 0) "-" else ""
        val balanceHours = abs(balance / 60)
        val balanceRemainingMinutes = abs(balance % 60)
        return String.format("%s%02d:%02d", sign, balanceHours, balanceRemainingMinutes)
    }

    private fun formatTimeWorked(timeWorkedMinutes: Long?): String? {
        return timeWorkedMinutes?.let {
            val hours = it / 60
            val minutes = it % 60
            String.format("%02d:%02d", hours, minutes)
        }
    }

    private fun convertToUpdateTimeRecordDto(savedUpdate: TimeRecord): UpdateTimeRecordDto {
        return UpdateTimeRecordDto(
            id = savedUpdate.id,
            startWorkTime = savedUpdate.startWorkTime?.format(DateTimeFormatter.ofPattern("HH:mm")),
            startWorkDate = savedUpdate.startWorkTime?.toLocalDate()?.format(DateTimeFormatter.ISO_DATE),
            endWorkTime = savedUpdate.endWorkTime?.format(DateTimeFormatter.ofPattern("HH:mm")),
            endWorkDate = savedUpdate.endWorkTime?.toLocalDate()?.format(DateTimeFormatter.ISO_DATE)
        )
    }

    private fun convertToDetailedTimeRecordDto(timeRecord: TimeRecord): DetailedTimeRecordDto {
        return DetailedTimeRecordDto(
            id = timeRecord.id,
            startWorkTime = DateUtils.formatTime(timeRecord.startWorkTime),
            endWorkTime = DateUtils.formatTime(timeRecord.endWorkTime),
            startWorkDate = DateUtils.formatDate(timeRecord.startWorkTime?.toLocalDate()),
            endWorkDate = DateUtils.formatDate(timeRecord.endWorkTime?.toLocalDate()),
            timeWorked = formatTimeWorked(timeRecord.timeWorked)
        )
    }
    private fun getCurrentUserId(): UUID {
        val authentication = SecurityContextHolder.getContext().authentication
        val token = authentication.credentials.toString()
        return jwtTokenUtil.getUserIdFromToken(token)
    }
}
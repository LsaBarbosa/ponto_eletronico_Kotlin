package br.com.santanna.ponto_eletronico.domain.service.impl

import br.com.santanna.ponto_eletronico.app.handler.model.DataIntegrityViolationException
import br.com.santanna.ponto_eletronico.app.handler.model.ObjectNotFoundException
import br.com.santanna.ponto_eletronico.domain.dataprovider.EmployeeDataProvider
import br.com.santanna.ponto_eletronico.domain.dataprovider.TimeRecordDataProvider
import br.com.santanna.ponto_eletronico.domain.dto.timeRecord.*
import br.com.santanna.ponto_eletronico.domain.entity.Employee
import br.com.santanna.ponto_eletronico.domain.entity.TimeRecord
import br.com.santanna.ponto_eletronico.domain.service.TimeRecordService
import jakarta.transaction.Transactional
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.abs
import kotlin.reflect.KMutableProperty1


private const val checkinException = "Necessário realizar o checkout para o chekin em aberto"

private const val zoneTime = "America/Sao_Paulo"

private const val checkoutException = "Não há checkin aberto para ser encerrado"

private const val timePattern = "HH:mm"

@Service
data class TimeRecordServiceImpl(
    private val timeRecordDataProvider: TimeRecordDataProvider, private val employeeDataProvider: EmployeeDataProvider,
    private val mapper: ModelMapper
): TimeRecordService {

    @Transactional
    override fun registerCheckin(cpf: String ): RecordCheckinDto? {
        val employee = findEmployeeByCpfOrThrow(cpf)

        val lastRecord =  findLastTimeRecord(employee)
        if (lastRecord != null) {
            throw Exception(checkinException)
        }

        val currentDateTimeInBrasilia = LocalDateTime.now(ZoneId.of(zoneTime))
        val newRegister = TimeRecord().apply {
            this.employee = employee
            this.startWorkTime = currentDateTimeInBrasilia
        }

        val savedCheckin = timeRecordDataProvider.save(newRegister)
        return createRecordCheckinDto(savedCheckin)
    }

    @Transactional
    override fun registerCheckout(cpf: String): RecordCheckoutDto? {
        val employee = findEmployeeByCpfOrThrow(cpf)

        val lastRecord = findLastTimeRecord(employee)
            ?: throw Exception(checkoutException)

        val currentDateTimeInBrasilia = LocalDateTime.now(ZoneId.of(zoneTime))
        lastRecord.endWorkTime = currentDateTimeInBrasilia

        val duration = Duration.between(lastRecord.startWorkTime, currentDateTimeInBrasilia)
        lastRecord.timeWorked = duration.toMinutes()

        val savedCheckout = timeRecordDataProvider.save(lastRecord)

        return RecordCheckoutDto(
            id = savedCheckout.id,
            endWorkTime = savedCheckout.endWorkTime?.format(DateTimeFormatter.ofPattern(timePattern)),
            timeWorked = formatTimeWorked(savedCheckout.timeWorked),
            endWorkDate = savedCheckout.endWorkTime?.toLocalDate()?.format(DateTimeFormatter.ISO_DATE)
        )
    }

    @Transactional
    override fun updateTimeRecord(updateTimeRecordRequestDto: UpdateTimeRecordRequestDto): UpdateTimeRecordDto {
        val employeeTarget = employeeDataProvider.findCpf(updateTimeRecordRequestDto.employeeCpfTarget)
            ?: throw IllegalArgumentException("Employee not found with CPF: ${updateTimeRecordRequestDto.employeeCpfTarget}")

        val updatingEmployee = employeeDataProvider.findCpf(updateTimeRecordRequestDto.employeeManagerCpf)
            ?: throw IllegalArgumentException("Employee not found with CPF: ${updateTimeRecordRequestDto.employeeManagerCpf}")

        val isPasswordValid = BCryptPasswordEncoder().matches(updateTimeRecordRequestDto.passwords, updatingEmployee.password)
        if (!isPasswordValid) {
            throw IllegalArgumentException("Invalid password")
        }

        val timeRecord = timeRecordDataProvider.findById(updateTimeRecordRequestDto.timeRecordId)
            ?: throw ObjectNotFoundException("Time record not found with ID: ${updateTimeRecordRequestDto.timeRecordId}")

        if (timeRecord.employee?.cpf != employeeTarget.cpf) {
            throw DataIntegrityViolationException("Registro não pertence ao cpf: ${updateTimeRecordRequestDto.employeeCpfTarget} informado")
        }

        updateTimeRecordField(timeRecord, updateTimeRecordRequestDto.updateTimeRecordDto.startWorkDate, updateTimeRecordRequestDto.updateTimeRecordDto.startWorkTime, TimeRecord::startWorkTime, "start")
        updateTimeRecordField(timeRecord, updateTimeRecordRequestDto.updateTimeRecordDto.endWorkDate, updateTimeRecordRequestDto.updateTimeRecordDto.endWorkTime, TimeRecord::endWorkTime, "end")

        val saveUpdate = timeRecordDataProvider.save(timeRecord)

        return UpdateTimeRecordDto(
            id = saveUpdate.id,
            startWorkTime = saveUpdate.startWorkTime?.format(DateTimeFormatter.ofPattern("HH:mm")),
            startWorkDate = saveUpdate.startWorkTime?.toLocalDate()?.format(DateTimeFormatter.ISO_DATE),
            endWorkTime = saveUpdate.endWorkTime?.format(DateTimeFormatter.ofPattern("HH:mm")),
            endWorkDate = saveUpdate.endWorkTime?.toLocalDate()?.format(DateTimeFormatter.ISO_DATE)
        )
    }



    override fun balanceHoursByDate(cpf: String, startDate: LocalDate, endDate: LocalDate): BalanceHoursDto {
        val timeRecords = findTimeRecordsByDateRange(cpf, startDate, endDate)
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
            employeeCpf = cpf,
            balance = formattedBalance
        )
    }

    override fun getTimeRecordsByEmployeeCpfAndDateRangePageable(
        cpf: String,
        startDate: LocalDate,
        endDate: LocalDate,
        pageable: Pageable
    ): Page<DetailedTimeRecordDto> {
        val startDateTime = startDate.atStartOfDay()
        val endDateTime = endDate.atTime(23, 59, 59)
        val timeRecords = timeRecordDataProvider.findByEmployeeCpfAndDateRange(cpf, startDateTime, endDateTime, pageable)
        return timeRecords.map { convertToDetailedTimeRecordDto(it) }
    }


    @Transactional
    override fun deleteTimeRecord(deleteTimeRecordRequestDto: DeleteTimeRecordRequestDto) {
        val employeeTarget = employeeDataProvider.findCpf(deleteTimeRecordRequestDto.employeeCpfTarget)
            ?: throw IllegalArgumentException("Employee not found with CPF: ${deleteTimeRecordRequestDto.employeeCpfTarget}")

        val deletingEmployee = employeeDataProvider.findCpf(deleteTimeRecordRequestDto.employeeManagerCpf)
            ?: throw IllegalArgumentException("Employee not found with CPF: ${deleteTimeRecordRequestDto.employeeManagerCpf}")

        val isPasswordValid = BCryptPasswordEncoder().matches(deleteTimeRecordRequestDto.passwords, deletingEmployee.password)
        if (!isPasswordValid) {
            throw IllegalArgumentException("Invalid password")
        }

        val timeRecord = timeRecordDataProvider.findById(deleteTimeRecordRequestDto.timeRecordId)
            ?: throw ObjectNotFoundException("Time record not found with ID: ${deleteTimeRecordRequestDto.timeRecordId}")

        if (timeRecord.employee?.cpf != employeeTarget.cpf) {
            throw DataIntegrityViolationException("Registro não pertence ao cpf: ${deleteTimeRecordRequestDto.employeeCpfTarget} informado")
        }

        timeRecordDataProvider.delete(timeRecord)
    }

    private fun findTimeRecordsByDateRange(cpf:String, startDate: LocalDate, endDate: LocalDate): List<TimeRecord> {
        val startDateTime = startDate.atStartOfDay()
        val endDateTime = endDate.atTime(23, 59, 59)
        return timeRecordDataProvider.findByEmployeeCpfAndDateRange(cpf, startDateTime, endDateTime)
    }

    private  fun findLastTimeRecord(employee: Employee?): TimeRecord? {
        return timeRecordDataProvider.findTopByEmployeeAndEndWorkTimeIsNullOrderByStartWorkTimeDesc(employee)
    }

    private  fun createRecordCheckinDto(saveCheckin: TimeRecord): RecordCheckinDto {
        return RecordCheckinDto(
            id = saveCheckin.id,
            startOfWorkTime = saveCheckin.startWorkTime?.format(DateTimeFormatter.ofPattern(timePattern)),
            startOfWorkDate = saveCheckin.startWorkTime?.toLocalDate()?.format(DateTimeFormatter.ISO_DATE)
        )
    }

    private  fun convertToDetailedTimeRecordDto(timeRecord: TimeRecord): DetailedTimeRecordDto {
        return DetailedTimeRecordDto(
            id = timeRecord.id,
            startWorkTime = timeRecord.startWorkTime?.format(DateTimeFormatter.ofPattern(timePattern)),
            endWorkTime = timeRecord.endWorkTime?.format(DateTimeFormatter.ofPattern(timePattern)),
            startWorkDate = timeRecord.startWorkTime?.toLocalDate()?.format(DateTimeFormatter.ISO_DATE),
            endWorkDate = timeRecord.endWorkTime?.toLocalDate()?.format(DateTimeFormatter.ISO_DATE),
            timeWorked = formatTimeWorked(timeRecord.timeWorked)
        )
    }

    private fun formatTimeWorked(timeWorkedMinutes: Long?): String? {
        return timeWorkedMinutes?.let {
            val hours = it / 60
            val minutes = it % 60
            String.format("%02d:%02d", hours, minutes)
        }
    }

    private  fun validateDateChange(currentDateTime: LocalDateTime?, newDateTime: LocalDateTime, dateType: String) {
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

    private fun updateTimeRecordField(
        timeRecord: TimeRecord,
        newDate: String?,
        newTime: String?,
        dateTimeField: KMutableProperty1<TimeRecord, LocalDateTime?>,
        dateType: String
    ) {
        if (newDate != null && newTime != null) {
            val newDateTime = LocalDateTime.parse("$newDate $newTime", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            validateDateChange(dateTimeField.get(timeRecord), newDateTime, dateType)
            dateTimeField.set(timeRecord, newDateTime)

            if (dateTimeField == TimeRecord::endWorkTime) {
                val duration = Duration.between(timeRecord.startWorkTime, newDateTime)
                timeRecord.timeWorked = duration.toMinutes()
            }
        }
    }
    private fun findEmployeeByCpfOrThrow(cpf: String): Employee {
        return employeeDataProvider.findCpf(cpf) ?: throw ObjectNotFoundException("Employee not found with CPF: $cpf")
    }
}

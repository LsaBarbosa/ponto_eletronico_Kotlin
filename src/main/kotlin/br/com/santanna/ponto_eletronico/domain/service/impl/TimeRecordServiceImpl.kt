package br.com.santanna.ponto_eletronico.domain.service.impl

import br.com.santanna.ponto_eletronico.app.handler.model.DataIntegrityViolationException
import br.com.santanna.ponto_eletronico.app.handler.model.ObjectNotFoundException
import br.com.santanna.ponto_eletronico.domain.dataprovider.EmployeeDataProvider
import br.com.santanna.ponto_eletronico.domain.dataprovider.TimeRecordDataProvider
import br.com.santanna.ponto_eletronico.domain.dto.timeRecord.*
import br.com.santanna.ponto_eletronico.domain.entity.TimeRecord
import br.com.santanna.ponto_eletronico.domain.entity.employee.EmployeeRole
import br.com.santanna.ponto_eletronico.domain.service.TimeRecordService
import br.com.santanna.ponto_eletronico.domain.service.util.timerecord.TimeRecordUtils
import br.com.santanna.ponto_eletronico.infrastructure.security.JwtTokenUtil
import jakarta.transaction.Transactional
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.abs


private const val CHECKIN_EXCEPTION = "Necessário realizar o checkout para o checkin em aberto"
private const val CHECKOUT_EXCEPTION = "Não há checkin aberto para ser encerrado"
private const val ZONE_TIME = "America/Sao_Paulo"


private const val TIME_RECORD_NOT_FOUND = "Registro de horas não encontrado no ID:"

@Service
data class TimeRecordServiceImpl(
    private val timeRecordDataProvider: TimeRecordDataProvider,
    private val employeeDataProvider: EmployeeDataProvider,
    private val mapper: ModelMapper,
    private val jwtTokenUtil: JwtTokenUtil,
    private val timeRecordUtils: TimeRecordUtils
) : TimeRecordService {

    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    @Transactional
    override fun registerCheckin(): RecordCheckinDto? {
        val id = timeRecordUtils.getCurrentUserId()
        val employee = employeeDataProvider.findById(id)

        timeRecordUtils.findLastTimeRecord(employee, timeRecordDataProvider)?.let {
            throw DataIntegrityViolationException(CHECKIN_EXCEPTION)
        }

        val newRegister = TimeRecord().apply {
            this.employee = employee
            this.startWorkTime = LocalDateTime.now(ZoneId.of(ZONE_TIME))
        }

        val savedCheckin = timeRecordDataProvider.save(newRegister)
        return timeRecordUtils.createRecordCheckinDto(savedCheckin)
    }

    @Transactional
    override fun registerCheckout(): RecordCheckoutDto? {
        val id = timeRecordUtils.getCurrentUserId()
        val employee = employeeDataProvider.findById(id)

        val lastRecord = timeRecordUtils.findLastTimeRecord(employee, timeRecordDataProvider)
            ?: throw Exception(CHECKOUT_EXCEPTION)
        val currentDateTimeInBrasilia = LocalDateTime.now(ZoneId.of(ZONE_TIME))

        lastRecord.endWorkTime = currentDateTimeInBrasilia
        lastRecord.timeWorked = Duration.between(lastRecord.startWorkTime, currentDateTimeInBrasilia).toMinutes()

        val savedCheckout = timeRecordDataProvider.save(lastRecord)
        return timeRecordUtils.createRecordCheckoutDto(savedCheckout)
    }

    @Transactional
    override fun updateTimeRecordAsManager(updateTimeRecordRequestDto: UpdateTimeRecordRequestDto): UpdateTimeRecordDto {
        val manager = timeRecordUtils.validateManager(updateTimeRecordRequestDto.passwords)
        timeRecordUtils.validateSameCompany(updateTimeRecordRequestDto.employeeCpfTarget, manager)

        val timeRecord = timeRecordDataProvider.findById(updateTimeRecordRequestDto.timeRecordId)
            ?: throw ObjectNotFoundException("$TIME_RECORD_NOT_FOUND ${updateTimeRecordRequestDto.timeRecordId}")

        updateTimeRecordRequestDto.updateTimeRecordDto.let {
            timeRecordUtils.updateRecordFields(timeRecord, it)
        }

        val savedUpdate = timeRecordDataProvider.save(timeRecord)
        return timeRecordUtils.convertToUpdateTimeRecordDto(savedUpdate)
    }

    override fun balanceHoursByDateAsManager(searchRequestDto: SearchByDateTimeRecordRequestDto): BalanceHoursDto {
        val manager = timeRecordUtils.validateManager(searchRequestDto.passwords)
        timeRecordUtils.validateSameCompany(searchRequestDto.employeeCpfTarget, manager)

        val startDate = searchRequestDto.searchByDateTimeRecordDto.startDate?.let { LocalDate.parse(it, dateFormatter) }
        val endDate = searchRequestDto.searchByDateTimeRecordDto.endDate?.let { LocalDate.parse(it, dateFormatter) }
        val timeRecords = timeRecordUtils.findTimeRecordsByDateRange(
            searchRequestDto.employeeCpfTarget,
            startDate!!,
            endDate!!,
            timeRecordDataProvider
        )
        val recordsByDate = timeRecords.groupBy { it.startWorkTime?.toLocalDate() }

        var totalWorkedMinutes = 0L
        var totalExpectedMinutes = 0L

        for ((_, records) in recordsByDate) {
            totalWorkedMinutes += records.sumOf { it.timeWorked ?: 0 }
            totalExpectedMinutes += 8 * 60
        }

        val balance = totalWorkedMinutes - totalExpectedMinutes
        val formattedBalance = timeRecordUtils.formatBalance(balance)

        return BalanceHoursDto(employeeCpf = searchRequestDto.employeeCpfTarget, balance = formattedBalance)
    }

    override fun getTimeRecordsByEmployeeCpfAndDateRangePageableAsManager(
        searchRequestDto: SearchByDateTimeRecordRequestDto,
        pageable: Pageable
    ): Page<DetailedTimeRecordDto> {
        val manager = timeRecordUtils.validateManager(searchRequestDto.passwords)
        timeRecordUtils.validateSameCompany(searchRequestDto.employeeCpfTarget, manager)

        val startDate = searchRequestDto.searchByDateTimeRecordDto.startDate?.let { LocalDate.parse(it, dateFormatter) }
        val endDate = searchRequestDto.searchByDateTimeRecordDto.endDate?.let { LocalDate.parse(it, dateFormatter) }

        val startDateTime = startDate?.atStartOfDay()
        val endDateTime = endDate?.atTime(23, 59, 59)

        val timeRecords = timeRecordDataProvider.findByEmployeeCpfAndDateRange(
            searchRequestDto.employeeCpfTarget,
            startDateTime!!,
            endDateTime!!,
            pageable
        )
        return timeRecords.map { timeRecordUtils.convertToDetailedTimeRecordDto(it) }
    }

    override fun getTimeRecordsByEmployeeCpfAndDateRangePageableToPDFAsManager(
        cpf: String,
        startDate: LocalDate,
        endDate: LocalDate,
        pageable: Pageable
    ): Page<DetailedTimeRecordDto> {
        val id = timeRecordUtils.getCurrentUserId()
        val manager = employeeDataProvider.findById(id)

        if (manager.role != EmployeeRole.MANAGER) {
            throw IllegalArgumentException("Colaborador não tem permissão")
        }
        timeRecordUtils.validateSameCompany(cpf,manager)
        val timeRecords = timeRecordDataProvider.findByEmployeeCpfAndDateRange(
            cpf, startDate.atStartOfDay(), endDate.atTime(23, 59, 59), pageable
        )
        return timeRecords.map { timeRecordUtils.convertToDetailedTimeRecordDto(it) }
    }

    override fun balanceHoursByDate(searchByDateTimeRecordDto: SearchByDateTimeRecordDto): BalanceHoursDto {
        val id = timeRecordUtils.getCurrentUserId()
        val employee = employeeDataProvider.findById(id)

        val startDate =  searchByDateTimeRecordDto.startDate?.let { LocalDate.parse(it, dateFormatter) }
        val endDate =  searchByDateTimeRecordDto.endDate?.let { LocalDate.parse(it, dateFormatter) }
        val timeRecords = timeRecordUtils.findTimeRecordsByDateRange(
            employee.cpf,
            startDate!!,
            endDate!!,
            timeRecordDataProvider
        )
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
            employeeCpf = employee.cpf,
            balance = formattedBalance
        )
    }

    override fun getTimeRecordsByEmployeeCpfAndDateRangePageable(
        searchByDateTimeRecordDto: SearchByDateTimeRecordDto,
        pageable: Pageable
    ): Page<DetailedTimeRecordDto> {
        val id = timeRecordUtils.getCurrentUserId()
        val employee = employeeDataProvider.findById(id)

        val startDate = searchByDateTimeRecordDto.startDate?.let { LocalDate.parse(it, dateFormatter) }
        val endDate = searchByDateTimeRecordDto.endDate?.let { LocalDate.parse(it, dateFormatter) }

        val startDateTime = startDate?.atStartOfDay()
        val endDateTime = endDate?.atTime(23, 59, 59)

        val timeRecords =
            timeRecordDataProvider.findByEmployeeCpfAndDateRange(employee.cpf, startDateTime!!, endDateTime!!, pageable)
        return timeRecords.map { timeRecordUtils.convertToDetailedTimeRecordDto(it) }
    }

    @Transactional
    override fun deleteTimeRecordAsManager(deleteTimeRecordRequestDto: DeleteTimeRecordRequestDto) {
        val manager = timeRecordUtils.validateManager(deleteTimeRecordRequestDto.passwords)
        timeRecordUtils.validateSameCompany(deleteTimeRecordRequestDto.employeeCpfTarget, manager)

        val timeRecord = timeRecordDataProvider.findById(deleteTimeRecordRequestDto.timeRecordId)
            ?: throw ObjectNotFoundException("$TIME_RECORD_NOT_FOUND ${deleteTimeRecordRequestDto.timeRecordId}")

        if (timeRecord.employee?.cpf != deleteTimeRecordRequestDto.employeeCpfTarget) {
            throw DataIntegrityViolationException("Registro não pertence ao cpf: ${deleteTimeRecordRequestDto.employeeCpfTarget} informado")
        }

        timeRecordDataProvider.delete(timeRecord)
    }
}
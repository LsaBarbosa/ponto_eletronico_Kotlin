package br.com.santanna.ponto_eletronico.domain.service.impl

import br.com.santanna.ponto_eletronico.app.handler.model.DataIntegrityViolationException
import br.com.santanna.ponto_eletronico.app.handler.model.ObjectNotFoundException
import br.com.santanna.ponto_eletronico.domain.dto.timeRecord.*
import br.com.santanna.ponto_eletronico.domain.entity.Employee
import br.com.santanna.ponto_eletronico.domain.entity.TimeRecord
import br.com.santanna.ponto_eletronico.domain.dataprovider.EmployeeDataProvider
import br.com.santanna.ponto_eletronico.domain.dataprovider.TimeRecordDataProvider
import br.com.santanna.ponto_eletronico.domain.service.TimeRecordService
import jakarta.transaction.*
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
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
    override fun updateTimeRecord(cpf :String, updateTimeRecordDto: UpdateTimeRecordDto): UpdateTimeRecordDto {
        val employee = findEmployeeByCpfOrThrow(cpf)
        val timeRecord = updateTimeRecordDto.id?.let { timeRecordDataProvider.findById(it) }

        if (timeRecord?.employee?.cpf != employee.cpf) {
            throw DataIntegrityViolationException("Registro não pertence ao cpf: $cpf informado")
        }


        if (timeRecord != null) {
            updateTimeRecordField(timeRecord, updateTimeRecordDto.startWorkDate, updateTimeRecordDto.startWorkTime, TimeRecord::startWorkTime, "start")
        }
        if (timeRecord != null) {
            updateTimeRecordField(timeRecord, updateTimeRecordDto.endWorkDate, updateTimeRecordDto.endWorkTime, TimeRecord::endWorkTime, "end")
        }

        val saveUpdate = timeRecord?.let { timeRecordDataProvider.save(it) }

        return UpdateTimeRecordDto(
            id = saveUpdate?.id,
            startWorkTime = saveUpdate?.startWorkTime?.format(DateTimeFormatter.ofPattern("HH:mm")),
            startWorkDate = saveUpdate?.startWorkTime?.toLocalDate()?.format(DateTimeFormatter.ISO_DATE),
            endWorkTime = saveUpdate?.endWorkTime?.format(DateTimeFormatter.ofPattern("HH:mm")),
            endWorkDate = saveUpdate?.endWorkTime?.toLocalDate()?.format(DateTimeFormatter.ISO_DATE)
        )
    }


    override fun overtimeByDate(cpf: String, startDate: LocalDate, endDate: LocalDate): OvertimeDto {
        val timeRecords = findTimeRecordsByDateRange(cpf, startDate, endDate)

        val totalMinutesWorked = timeRecords.sumOf { it.timeWorked ?: 0 }
        val totalExpectedMinutes = timeRecords.size * 8 * 60

        val overtimeMinutes = maxOf(0, totalMinutesWorked - totalExpectedMinutes)

        val overtimeHours = overtimeMinutes / 60
        val overtimeRemainingMinutes = overtimeMinutes % 60

        return OvertimeDto(
            employeeCpf = cpf,
            overtime = String.format("%02d:%02d", overtimeHours, overtimeRemainingMinutes)
        )
    }

    override fun getTimeRecordsByEmployeeCpfAndDateRange(cpf: String, startDate: LocalDate, endDate: LocalDate): List<DetailedTimeRecordDto> {
        val timeRecords = findTimeRecordsByDateRange(cpf, startDate, endDate)
        return timeRecords.map { convertToDetailedTimeRecordDto(it) }
    }

    override fun getTimeRecordsByEmployeeCpfAndDateRange(cpf: String, startDate: LocalDate, endDate: LocalDate, pageable: Pageable): Page<DetailedTimeRecordDto> {
        val startDateTime = startDate.atStartOfDay()
        val endDateTime = endDate.atTime(23, 59, 59)
        val timeRecords = timeRecordDataProvider.findByEmployeeCpfAndDateRange(cpf, startDateTime, endDateTime)

        val detailedTimeRecords = timeRecords.map { convertToDetailedTimeRecordDto(it) }
        val pagedResult = detailedTimeRecords.subList(
            pageable.pageNumber * pageable.pageSize,
            Math.min((pageable.pageNumber + 1) * pageable.pageSize, detailedTimeRecords.size)
        )

        return PageImpl(pagedResult, pageable, detailedTimeRecords.size.toLong())
    }

    @Transactional
    override fun deleteTimeRecord(cpf: String, timeRecordId: Long) {
        val employee = findEmployeeByCpfOrThrow(cpf)
        val timeRecord = timeRecordDataProvider.findById(timeRecordId) ?: throw ObjectNotFoundException("Time record not found with ID: $timeRecordId")

        if (timeRecord.employee?.cpf != employee.cpf) {
            throw DataIntegrityViolationException("Registro não pertence ao cpf: $cpf informado")
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

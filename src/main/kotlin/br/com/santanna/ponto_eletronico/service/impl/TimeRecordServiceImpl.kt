package br.com.santanna.ponto_eletronico.service.impl

import br.com.santanna.ponto_eletronico.model.Employee
import br.com.santanna.ponto_eletronico.model.TimeRecord
import br.com.santanna.ponto_eletronico.model.dto.timeRecord.*
import br.com.santanna.ponto_eletronico.repository.EmployeeRepository
import br.com.santanna.ponto_eletronico.repository.TimeRecordRepository
import br.com.santanna.ponto_eletronico.repository.dataProvider.EmployeeDataProvider
import br.com.santanna.ponto_eletronico.repository.dataProvider.TimeRecordDataProvider
import br.com.santanna.ponto_eletronico.service.TimeRecordService
import br.com.santanna.ponto_eletronico.service.exception.DataIntegrityViolationException
import org.modelmapper.ModelMapper
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

    override fun registerCheckin(name: String, surname: String): RecordCheckinDto? {
        val employee = employeeDataProvider.findByNameAndSurnameIgnoreCase(name, surname)
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

    override fun registerCheckout(name: String, surname: String): RecordCheckoutDto? {
        val employee = employeeDataProvider.findByNameAndSurnameIgnoreCase(name, surname)
            ?: throw Exception("Employee not found")
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

    override fun updateTimeRecord(updateTimeRecordDto: UpdateTimeRecordDto): UpdateTimeRecordDto {
        val timeRecord = timeRecordDataProvider.updateTimeRecordById(updateTimeRecordDto.id)


        updateTimeRecordField(timeRecord, updateTimeRecordDto.startWorkDate, updateTimeRecordDto.startWorkTime, TimeRecord::startWorkTime, "start")
        updateTimeRecordField(timeRecord, updateTimeRecordDto.endWorkDate, updateTimeRecordDto.endWorkTime, TimeRecord::endWorkTime, "end")

        val saveUpdate = timeRecordDataProvider.save(timeRecord)

        return UpdateTimeRecordDto(
            id = saveUpdate.id!!,
            startWorkTime = saveUpdate.startWorkTime?.format(DateTimeFormatter.ofPattern(timePattern)),
            startWorkDate = saveUpdate.startWorkTime?.toLocalDate()?.format(DateTimeFormatter.ISO_DATE),
            endWorkTime = saveUpdate.endWorkTime?.format(DateTimeFormatter.ofPattern(timePattern)),
            endWorkDate = saveUpdate.endWorkTime?.toLocalDate()?.format(DateTimeFormatter.ISO_DATE)
        )
    }

    override fun overtimeByDate(name: String, surname: String, startDate: LocalDate, endDate: LocalDate): OvertimeDto {
        val timeRecords = findTimeRecordsByDateRange(name, surname, startDate, endDate)

        val totalMinutesWorked = timeRecords.sumOf { it.timeWorked ?: 0 }
        val totalExpectedMinutes = timeRecords.size * 8 * 60

        val overtimeMinutes = maxOf(0, totalMinutesWorked - totalExpectedMinutes)

        val overtimeHours = overtimeMinutes / 60
        val overtimeRemainingMinutes = overtimeMinutes % 60

        return OvertimeDto(
            employeeName = name,
            overtime = String.format("%02d:%02d", overtimeHours, overtimeRemainingMinutes)
        )
    }

    override fun getTimeRecordsByEmployeeNameAndDateRange(name: String, surname: String, startDate: LocalDate, endDate: LocalDate): List<DetailedTimeRecordDto> {
        val timeRecords = findTimeRecordsByDateRange(name, surname, startDate, endDate)
        return timeRecords.map { convertToDetailedTimeRecordDto(it) }
    }

    private fun findTimeRecordsByDateRange(name: String, surname: String, startDate: LocalDate, endDate: LocalDate): List<TimeRecord> {
        val startDateTime = startDate.atStartOfDay()
        val endDateTime = endDate.atTime(23, 59, 59)
        return timeRecordDataProvider.findByEmployeeNameAndDateRange(name, surname, startDateTime, endDateTime)
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
            throw Exception("$dateType date cannot be null")
        }

        if (currentDateTime.year != newDateTime.year) {
            throw Exception("Year cannot be changed for $dateType date")
        }

        if (currentDateTime.month != newDateTime.month) {
            throw Exception("Month cannot be changed for $dateType date")
        }

        if (newDateTime.dayOfMonth > currentDateTime.dayOfMonth) {
            throw Exception("Day cannot be changed to a future date for $dateType date")
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
}

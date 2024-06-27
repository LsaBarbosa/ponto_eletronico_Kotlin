package br.com.santanna.domain.service.impl


import br.com.santanna.domain.dataprovider.EmployeeDataProvider
import br.com.santanna.domain.dataprovider.TimeRecordDataProvider
import br.com.santanna.domain.dto.timeRecord.*
import br.com.santanna.domain.entity.Employee
import br.com.santanna.domain.entity.TimeRecord
import br.com.santanna.domain.service.TimeRecordService
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.reflect.KMutableProperty1




@Service
data class TimeRecordServiceImpl(
    private val timeRecordsDataProvider: TimeRecordDataProvider, private val employeeDataProvider: EmployeeDataProvider,
): TimeRecordService {

   companion object {
        private const val CHECKINEXCEPTION = "Necessário realizar o checkout para o chekin em aberto"

        private const val TIMEZONE = "America/Sao_Paulo"

        private const val CHECKOUTNEXCEPTION = "Não há checkin aberto para ser encerrado"

        private const val TIMEPATTERN = "HH:mm"
    }
    override fun registerCheckin(name: String, surname: String): RecordCheckinDto? {
        val employee = employeeDataProvider.findByNameAndSurnameIgnoreCase(name, surname)
        val lastRecord =  findLastTimeRecord(employee)
        if (lastRecord != null) {
            throw Exception(CHECKINEXCEPTION)
        }

        val currentDateTimeInBrasilia = LocalDateTime.now(ZoneId.of(TIMEZONE))
        val newRegister = TimeRecord().apply {
            this.employee = employee
            this.startWorkTime = currentDateTimeInBrasilia
        }

        val savedCheckin = timeRecordsDataProvider.save(newRegister)
        return createRecordCheckinDto(savedCheckin)
    }

    override fun registerCheckout(name: String, surname: String): RecordCheckoutDto? {
        val employee = employeeDataProvider.findByNameAndSurnameIgnoreCase(name, surname)
            ?: throw Exception("Employee not found")
        val lastRecord = findLastTimeRecord(employee)
            ?: throw Exception(CHECKOUTNEXCEPTION)

        val currentDateTimeInBrasilia = LocalDateTime.now(ZoneId.of(TIMEZONE))
        lastRecord.endWorkTime = currentDateTimeInBrasilia

        val duration = Duration.between(lastRecord.startWorkTime, currentDateTimeInBrasilia)
        lastRecord.timeWorked = duration.toMinutes()

        val savedCheckout = timeRecordsDataProvider.save(lastRecord)

        return RecordCheckoutDto(
            id = savedCheckout.id,
            endWorkTime = savedCheckout.endWorkTime?.format(DateTimeFormatter.ofPattern(TIMEPATTERN)),
            timeWorked = formatTimeWorked(savedCheckout.timeWorked),
            endWorkDate = savedCheckout.endWorkTime?.toLocalDate()?.format(DateTimeFormatter.ISO_DATE)
        )
    }

    override fun updateTimeRecord(updateTimeRecordDto: UpdateTimeRecordDto): UpdateTimeRecordDto {
        val timeRecord = timeRecordsDataProvider.updateTimeRecordById(updateTimeRecordDto.id)


        updateTimeRecordField(timeRecord, updateTimeRecordDto.startWorkDate, updateTimeRecordDto.startWorkTime, TimeRecord::startWorkTime, "start")
        updateTimeRecordField(timeRecord, updateTimeRecordDto.endWorkDate, updateTimeRecordDto.endWorkTime, TimeRecord::endWorkTime, "end")

        val saveUpdate = timeRecordsDataProvider.save(timeRecord)

        return UpdateTimeRecordDto(
            id = saveUpdate.id!!,
            startWorkTime = saveUpdate.startWorkTime?.format(DateTimeFormatter.ofPattern(TIMEPATTERN)),
            startWorkDate = saveUpdate.startWorkTime?.toLocalDate()?.format(DateTimeFormatter.ISO_DATE),
            endWorkTime = saveUpdate.endWorkTime?.format(DateTimeFormatter.ofPattern(TIMEPATTERN)),
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
        return timeRecordsDataProvider.findByEmployeeNameAndDateRange(name, surname, startDateTime, endDateTime)
    }

    private  fun findLastTimeRecord(employee: Employee?): TimeRecord? {
        return timeRecordsDataProvider.findTopByEmployeeAndEndWorkTimeIsNullOrderByStartWorkTimeDesc(employee)
    }

    private  fun createRecordCheckinDto(saveCheckin: TimeRecord): RecordCheckinDto {
        return RecordCheckinDto(
            id = saveCheckin.id,
            startOfWorkTime = saveCheckin.startWorkTime?.format(DateTimeFormatter.ofPattern(TIMEPATTERN)),
            startOfWorkDate = saveCheckin.startWorkTime?.toLocalDate()?.format(DateTimeFormatter.ISO_DATE)
        )
    }

    private  fun convertToDetailedTimeRecordDto(timeRecord: TimeRecord): DetailedTimeRecordDto {
        return DetailedTimeRecordDto(
            id = timeRecord.id,
            startWorkTime = timeRecord.startWorkTime?.format(DateTimeFormatter.ofPattern(TIMEPATTERN)),
            endWorkTime = timeRecord.endWorkTime?.format(DateTimeFormatter.ofPattern(TIMEPATTERN)),
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

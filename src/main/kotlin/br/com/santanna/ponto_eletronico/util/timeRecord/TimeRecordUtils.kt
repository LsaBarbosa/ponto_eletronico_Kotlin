package br.com.santanna.ponto_eletronico.util.timeRecord

import br.com.santanna.ponto_eletronico.model.Employee
import br.com.santanna.ponto_eletronico.model.TimeRecord
import br.com.santanna.ponto_eletronico.model.dto.timeRecord.DetailedTimeRecordDto
import br.com.santanna.ponto_eletronico.model.dto.timeRecord.RecordCheckinDto
import br.com.santanna.ponto_eletronico.repository.TimeRecordRepository
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.KMutableProperty1

object TimeRecordUtils {

    fun findLastTimeRecord(employee: Employee, timeRepository: TimeRecordRepository): TimeRecord? {
        return timeRepository.findTopByEmployeeAndEndWorkTimeIsNullOrderByStartWorkTimeDesc(employee)
    }

    fun createRecordCheckinDto(saveCheckin: TimeRecord): RecordCheckinDto {
        return RecordCheckinDto(
            id = saveCheckin.id,
            startOfWorkTime = saveCheckin.startWorkTime?.format(DateTimeFormatter.ofPattern("HH:mm")),
            startOfWorkDate = saveCheckin.startWorkTime?.toLocalDate()?.format(DateTimeFormatter.ISO_DATE)
        )
    }

    fun convertToDetailedTimeRecordDto(timeRecord: TimeRecord): DetailedTimeRecordDto {
        return DetailedTimeRecordDto(
            id = timeRecord.id,
            startWorkTime = timeRecord.startWorkTime?.format(DateTimeFormatter.ofPattern("HH:mm")),
            endWorkTime = timeRecord.endWorkTime?.format(DateTimeFormatter.ofPattern("HH:mm")),
            startWorkDate = timeRecord.startWorkTime?.toLocalDate()?.format(DateTimeFormatter.ISO_DATE),
            endWorkDate = timeRecord.endWorkTime?.toLocalDate()?.format(DateTimeFormatter.ISO_DATE),
            timeWorked = formatTimeWorked(timeRecord.timeWorked)
        )
    }

    fun formatTimeWorked(timeWorkedMinutes: Long?): String? {
        return timeWorkedMinutes?.let {
            val hours = it / 60
            val minutes = it % 60
            String.format("%02d:%02d", hours, minutes)
        }
    }

    fun validateDateChange(currentDateTime: LocalDateTime?, newDateTime: LocalDateTime, dateType: String) {
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

    fun updateTimeRecordField(
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

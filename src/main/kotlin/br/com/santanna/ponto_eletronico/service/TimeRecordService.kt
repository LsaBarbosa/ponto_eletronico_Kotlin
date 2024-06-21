package br.com.santanna.ponto_eletronico.service

import br.com.santanna.ponto_eletronico.model.TimeRecord
import br.com.santanna.ponto_eletronico.model.dto.timeRecord.*
import br.com.santanna.ponto_eletronico.repository.TimeRecordRepository
import org.modelmapper.ModelMapper
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@Service
data class TimeRecordService(
    private val timeRepository: TimeRecordRepository, private val employeeService: EmployeeService,
    private val mapper: ModelMapper
) {

    fun registerCheckin(name: String): RecordCheckinDto? {
        val employee = employeeService.getEmployeeByName(name) ?: throw Exception("Employee not found")

        val lastRecord = timeRepository.findTopByEmployeeAndEndWorkTimeIsNullOrderByStartWorkTimeDesc(employee)
        if (lastRecord != null) {
            throw Exception("Cannot check in without checking out the last time record.")
        }
        val nowInBrasilia = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"))
        val newRegister = TimeRecord().apply {
            this.employee = employee
            this.startWorkTime = nowInBrasilia
        }

        val saveCheckin = timeRepository.save(newRegister)
        return RecordCheckinDto(
            id = saveCheckin.id,
            startOfWorkTime = saveCheckin.startWorkTime?.format(DateTimeFormatter.ofPattern("HH:mm")),
            startOfWorkDate = saveCheckin.startWorkTime?.toLocalDate()?.format(DateTimeFormatter.ISO_DATE)
        )
    }

    fun registerCheckout(name: String): RecordCheckoutDto? {
        val employee = employeeService.getEmployeeByName(name) ?: throw Exception("Employee not found")


        val lastRecord = timeRepository.findTopByEmployeeAndEndWorkTimeIsNullOrderByStartWorkTimeDesc(employee)
            ?: throw Exception("No check-in record found to check out.")

        val nowInBrasilia = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"))
        lastRecord.endWorkTime = nowInBrasilia

        val duration = Duration.between(lastRecord.startWorkTime, lastRecord.endWorkTime)
        lastRecord.timeWorked = duration.toMinutes()

        val saveCheckout = timeRepository.save(lastRecord)

        val hoursWorked = duration.toHours()
        val minutesWorked = duration.toMinutes() % 60
        val formattedWorkTime = String.format("%02d:%02d", hoursWorked, minutesWorked)

        return RecordCheckoutDto(
            id = saveCheckout.id,
            endWorkTime = saveCheckout.endWorkTime?.format(DateTimeFormatter.ofPattern("HH:mm")),
            timeWorked = formattedWorkTime,
            endWorkDate = saveCheckout.endWorkTime?.toLocalDate()?.format(DateTimeFormatter.ISO_DATE)
        )
    }

    fun updateTimeRecord(updateTimeRecordDto: UpdateTimeRecordDto): UpdateTimeRecordDto {
        val timeRecord = timeRepository.findById(updateTimeRecordDto.id).orElseThrow { Exception("TimeRecord not found") }

        updateTimeRecordDto.startWorkDate?.let { date ->
            updateTimeRecordDto.startWorkTime?.let { time ->
                val startWorkDateTime = LocalDateTime.parse("$date $time", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                validateDateChange(timeRecord.startWorkTime, startWorkDateTime, "start")
                timeRecord.startWorkTime = startWorkDateTime
            }
        }

        updateTimeRecordDto.endWorkDate?.let { date ->
            updateTimeRecordDto.endWorkTime?.let { time ->
                val endWorkDateTime = LocalDateTime.parse("$date $time", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                validateDateChange(timeRecord.endWorkTime, endWorkDateTime, "end")
                timeRecord.endWorkTime = endWorkDateTime
                val duration = java.time.Duration.between(timeRecord.startWorkTime, endWorkDateTime)
                timeRecord.timeWorked = duration.toMinutes()
            }
        }
     val saveUpdate=   timeRepository.save(timeRecord)

        return UpdateTimeRecordDto(
            id = saveUpdate.id!!,
            startWorkTime = saveUpdate.startWorkTime?.format(DateTimeFormatter.ofPattern("HH:mm")),
            startWorkDate = saveUpdate.startWorkTime?.toLocalDate()?.format(DateTimeFormatter.ISO_DATE),
            endWorkTime = saveUpdate.endWorkTime?.format(DateTimeFormatter.ofPattern("HH:mm")),
            endWorkDate = saveUpdate.endWorkTime?.toLocalDate()?.format(DateTimeFormatter.ISO_DATE))
    }

    fun overtimeByDate(name: String, startDate: LocalDate, endDate: LocalDate): OvertimeDto {
        val startDateTime = startDate.atStartOfDay()
        val endDateTime = endDate.atTime(23, 59, 59)

        val timeRecords = timeRepository.findByEmployeeNameAndDateRange(name, startDateTime, endDateTime)

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



    fun getTimeRecordsByEmployeeNameAndDateRange(name: String, startDate: LocalDate, endDate: LocalDate): List<DetailedTimeRecordDto> {
        val startDateTime = startDate.atStartOfDay()
        val endDateTime = endDate.atTime(23, 59, 59)
        val timeRecords = timeRepository.findByEmployeeNameAndDateRange(name, startDateTime, endDateTime)

        return timeRecords.map { timeRecord ->
            DetailedTimeRecordDto(
                id = timeRecord.id,
                startWorkTime = timeRecord.startWorkTime?.format(DateTimeFormatter.ofPattern("HH:mm")),
                endWorkTime = timeRecord.endWorkTime?.format(DateTimeFormatter.ofPattern("HH:mm")),
                startWorkDate = timeRecord.startWorkTime?.toLocalDate()?.format(DateTimeFormatter.ISO_DATE),
                endWorkDate = timeRecord.endWorkTime?.toLocalDate()?.format(DateTimeFormatter.ISO_DATE),
                timeWorked = formatTimeWorked(timeRecord.timeWorked)
            )
        }
    }

    private fun formatTimeWorked(timeWorkedMinutes: Long?): String? {
        return timeWorkedMinutes?.let {
            val hours = it / 60
            val minutes = it % 60
            String.format("%02d:%02d", hours, minutes)
        }
    }
    private fun validateDateChange(currentDateTime: LocalDateTime?, newDateTime: LocalDateTime, dateType: String) {
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
}

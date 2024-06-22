package br.com.santanna.ponto_eletronico.service

import br.com.santanna.ponto_eletronico.model.Employee
import br.com.santanna.ponto_eletronico.model.TimeRecord
import br.com.santanna.ponto_eletronico.model.dto.employee.EmployeeGetDto
import br.com.santanna.ponto_eletronico.model.dto.timeRecord.*
import br.com.santanna.ponto_eletronico.repository.EmployeeRepository
import br.com.santanna.ponto_eletronico.repository.TimeRecordRepository
import org.modelmapper.ModelMapper
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.reflect.KMutableProperty1


@Service
data class TimeRecordService(
    private val timeRepository: TimeRecordRepository, private val employeeService: EmployeeService,
    private val mapper: ModelMapper, private val employeeRepository: EmployeeRepository
) {

    fun registerCheckin(name: String, surname:String): RecordCheckinDto? {
        val employee = employeeRepository.findByNameAndSurnameIgnoreCase(name, surname) ?: throw Exception("Employee not found")
        val lastRecord = findLastTimeRecord(employee)
        if (lastRecord != null) {
            throw Exception("Cannot check in without checking out the last time record.")
        }

        val currentDateTimeInBrasilia = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"))
        val newRegister = TimeRecord().apply {
            this.employee = employee
            this.startWorkTime = currentDateTimeInBrasilia
        }

        val saveCheckin = timeRepository.save(newRegister)
        return RecordCheckinDto(
            id = saveCheckin.id,
            startOfWorkTime = saveCheckin.startWorkTime?.format(DateTimeFormatter.ofPattern("HH:mm")),
            startOfWorkDate = saveCheckin.startWorkTime?.toLocalDate()?.format(DateTimeFormatter.ISO_DATE)
        )
    }

    fun registerCheckout(name: String, surname:String): RecordCheckoutDto? {
        val employee = employeeRepository.findByNameAndSurnameIgnoreCase(name, surname) ?: throw Exception("Employee not found")
        val lastRecord = findLastTimeRecord(employee)
            ?: throw Exception("No check-in record found to check out.")

        val currentDateTimeInBrasilia = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"))
        lastRecord.endWorkTime = currentDateTimeInBrasilia

        val duration = Duration.between(lastRecord.startWorkTime, currentDateTimeInBrasilia)
        lastRecord.timeWorked = duration.toMinutes()

        val savedCheckout = timeRepository.save(lastRecord)

        return RecordCheckoutDto(
            id = savedCheckout.id,
            endWorkTime = savedCheckout.endWorkTime?.format(DateTimeFormatter.ofPattern("HH:mm")),
            timeWorked = formatTimeWorked(savedCheckout.timeWorked),
            endWorkDate = savedCheckout.endWorkTime?.toLocalDate()?.format(DateTimeFormatter.ISO_DATE)
        )
    }

    fun updateTimeRecord(updateTimeRecordDto: UpdateTimeRecordDto): UpdateTimeRecordDto {
        val timeRecord = timeRepository.findById(updateTimeRecordDto.id).orElseThrow { Exception("TimeRecord not found") }

        updateTimeRecordField(timeRecord, updateTimeRecordDto.startWorkDate, updateTimeRecordDto.startWorkTime, TimeRecord::startWorkTime, "start")
        updateTimeRecordField(timeRecord, updateTimeRecordDto.endWorkDate, updateTimeRecordDto.endWorkTime, TimeRecord::endWorkTime, "end")

        val saveUpdate=   timeRepository.save(timeRecord)

        return UpdateTimeRecordDto(
            id = saveUpdate.id!!,
            startWorkTime = saveUpdate.startWorkTime?.format(DateTimeFormatter.ofPattern("HH:mm")),
            startWorkDate = saveUpdate.startWorkTime?.toLocalDate()?.format(DateTimeFormatter.ISO_DATE),
            endWorkTime = saveUpdate.endWorkTime?.format(DateTimeFormatter.ofPattern("HH:mm")),
            endWorkDate = saveUpdate.endWorkTime?.toLocalDate()?.format(DateTimeFormatter.ISO_DATE))
    }

    fun overtimeByDate(name: String,surname: String, startDate: LocalDate, endDate: LocalDate): OvertimeDto {
        val timeRecords =findTimeRecordsByDateRange(name,surname,startDate,endDate)

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

    fun getTimeRecordsByEmployeeNameAndDateRange(name: String, surname:String, startDate: LocalDate, endDate: LocalDate): List<DetailedTimeRecordDto> {

        val timeRecords =findTimeRecordsByDateRange(name,surname,startDate,endDate)

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
    private fun findLastTimeRecord(employee: Employee): TimeRecord? {
        val employee = employeeRepository.findById(employee.id!!).orElseThrow { Exception("Employee not found") } // Buscar o Employee pelo ID
        return timeRepository.findTopByEmployeeAndEndWorkTimeIsNullOrderByStartWorkTimeDesc(employee)
    }

    private fun findTimeRecordsByDateRange(name: String,surname: String, startDate: LocalDate, endDate: LocalDate): List<TimeRecord> {
        val startDateTime = startDate.atStartOfDay()
        val endDateTime = endDate.atTime(23, 59, 59)
        return timeRepository.findByEmployeeNameAndDateRange(name,surname, startDateTime, endDateTime)
    }
    private fun updateTimeRecordField(timeRecord: TimeRecord, newDate: String?, newTime: String?, dateTimeField: KMutableProperty1<TimeRecord, LocalDateTime?>, dateType: String) {
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

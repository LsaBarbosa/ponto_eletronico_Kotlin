package br.com.santanna.ponto_eletronico.service.impl

import br.com.santanna.ponto_eletronico.model.TimeRecord
import br.com.santanna.ponto_eletronico.model.dto.timeRecord.*
import br.com.santanna.ponto_eletronico.repository.EmployeeRepository
import br.com.santanna.ponto_eletronico.repository.TimeRecordRepository
import br.com.santanna.ponto_eletronico.service.TimeRecordService
import br.com.santanna.ponto_eletronico.util.timeRecord.TimeRecordUtils
import org.modelmapper.ModelMapper
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@Service
data class TimeRecordServiceImpl(
    private val timeRepository: TimeRecordRepository, private val employeeServiceImpl: EmployeeServiceImpl,
    private val mapper: ModelMapper, private val employeeRepository: EmployeeRepository
): TimeRecordService {

    override fun registerCheckin(name: String, surname: String): RecordCheckinDto? {
        val employee = employeeRepository.findByNameAndSurnameIgnoreCase(name, surname)
            ?: throw Exception("Employee not found")
        val lastRecord = TimeRecordUtils.findLastTimeRecord(employee, timeRepository)
        if (lastRecord != null) {
            throw Exception("Cannot check in without checking out the last time record.")
        }

        val currentDateTimeInBrasilia = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"))
        val newRegister = TimeRecord().apply {
            this.employee = employee
            this.startWorkTime = currentDateTimeInBrasilia
        }

        val savedCheckin = timeRepository.save(newRegister)
        return TimeRecordUtils.createRecordCheckinDto(savedCheckin)
    }

    override fun registerCheckout(name: String, surname: String): RecordCheckoutDto? {
        val employee = employeeRepository.findByNameAndSurnameIgnoreCase(name, surname)
            ?: throw Exception("Employee not found")
        val lastRecord = TimeRecordUtils.findLastTimeRecord(employee, timeRepository)
            ?: throw Exception("No check-in record found to check out.")

        val currentDateTimeInBrasilia = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"))
        lastRecord.endWorkTime = currentDateTimeInBrasilia

        val duration = Duration.between(lastRecord.startWorkTime, currentDateTimeInBrasilia)
        lastRecord.timeWorked = duration.toMinutes()

        val savedCheckout = timeRepository.save(lastRecord)

        return RecordCheckoutDto(
            id = savedCheckout.id,
            endWorkTime = savedCheckout.endWorkTime?.format(DateTimeFormatter.ofPattern("HH:mm")),
            timeWorked = TimeRecordUtils.formatTimeWorked(savedCheckout.timeWorked),
            endWorkDate = savedCheckout.endWorkTime?.toLocalDate()?.format(DateTimeFormatter.ISO_DATE)
        )
    }

    override fun updateTimeRecord(updateTimeRecordDto: UpdateTimeRecordDto): UpdateTimeRecordDto {
        val timeRecord = timeRepository.findById(updateTimeRecordDto.id)
            .orElseThrow { Exception("TimeRecord not found") }

        TimeRecordUtils.updateTimeRecordField(timeRecord, updateTimeRecordDto.startWorkDate, updateTimeRecordDto.startWorkTime, TimeRecord::startWorkTime, "start")
        TimeRecordUtils.updateTimeRecordField(timeRecord, updateTimeRecordDto.endWorkDate, updateTimeRecordDto.endWorkTime, TimeRecord::endWorkTime, "end")

        val saveUpdate = timeRepository.save(timeRecord)

        return UpdateTimeRecordDto(
            id = saveUpdate.id!!,
            startWorkTime = saveUpdate.startWorkTime?.format(DateTimeFormatter.ofPattern("HH:mm")),
            startWorkDate = saveUpdate.startWorkTime?.toLocalDate()?.format(DateTimeFormatter.ISO_DATE),
            endWorkTime = saveUpdate.endWorkTime?.format(DateTimeFormatter.ofPattern("HH:mm")),
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
        return timeRecords.map { TimeRecordUtils.convertToDetailedTimeRecordDto(it) }
    }

    private fun findTimeRecordsByDateRange(name: String, surname: String, startDate: LocalDate, endDate: LocalDate): List<TimeRecord> {
        val startDateTime = startDate.atStartOfDay()
        val endDateTime = endDate.atTime(23, 59, 59)
        return timeRepository.findByEmployeeNameAndDateRange(name, surname, startDateTime, endDateTime)
    }

}

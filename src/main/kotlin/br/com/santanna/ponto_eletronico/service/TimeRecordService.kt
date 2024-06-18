package br.com.santanna.ponto_eletronico.service

import br.com.santanna.ponto_eletronico.model.TimeRecord
import br.com.santanna.ponto_eletronico.model.dto.timeRecord.DetailedTimeRecordDto
import br.com.santanna.ponto_eletronico.model.dto.timeRecord.OvertimeDto
import br.com.santanna.ponto_eletronico.model.dto.timeRecord.RecordCheckinDto
import br.com.santanna.ponto_eletronico.model.dto.timeRecord.RecordCheckoutDto
import br.com.santanna.ponto_eletronico.repository.TimeRecordRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime


@Service
data class TimeRecordService(
    private val timeRepository: TimeRecordRepository,
    private val employeeService: EmployeeService
) {

    fun registerCheckin(name: String): RecordCheckinDto? {
        val employee = employeeService.getEmployeeByName(name)
        val activedCheckin = timeRepository.findRegistrationCheckInActive(name)

        if (activedCheckin.endWorkTime == null) {
            throw Exception("Feche o registro de entrada")
        }

        val newRegister = TimeRecord().apply {
            this.employee = employee
            this.startWorkTime = LocalDateTime.now()
        }

        val saveCheckin = timeRepository.save(newRegister)
        return RecordCheckinDto(saveCheckin)

    }

    fun registerCheckout(name: String): RecordCheckoutDto? {
        val employee = employeeService.getEmployeeByName(name)
        val recordCheckinActivated: TimeRecord? = employee?.let {
            timeRepository.findTopByEmployeeAndEndWorkTimeIsNullOrderByStartWorkTimeDesc(it)
        }

        if (recordCheckinActivated == null){
            throw Exception("Feche o registro de entrada")
        }
        val newCheckout = LocalDateTime.now()
        recordCheckinActivated.endWorkTime = newCheckout
        val saveCheckout = timeRepository.save(recordCheckinActivated)
            return RecordCheckoutDto()
        }

    fun overtimeByDate(name: String, startDate: LocalDate, endDate: LocalDate): OvertimeDto? {
        val employee = employeeService.getEmployeeByName(name)
        val startDateTime = startDate.atStartOfDay()
        val endDateTime = endDate.plusDays(1).atStartOfDay()

        val records: List<TimeRecord>? =
            employee?.let { timeRepository.findByEmployeeAndStartWorkTimeBetween(it, startDateTime, endDateTime) }

        return OvertimeDto()
    }

    fun searchRecordsByDate(name: String, startDate: LocalDate, endDate: LocalDate): List<DetailedTimeRecordDto>? {
        val employee = employeeService.getEmployeeByName(name)
        val startDateTime = startDate.atStartOfDay()
        val endDateTime = endDate.plusDays(1).atStartOfDay()

        val records: List<TimeRecord>? =
            employee?.let { timeRepository.findByEmployeeAndStartWorkTimeBetween(it, startDateTime, endDateTime) }
        return listOf(DetailedTimeRecordDto())
    }
}

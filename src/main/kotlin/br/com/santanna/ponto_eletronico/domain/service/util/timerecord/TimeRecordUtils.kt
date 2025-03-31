package br.com.santanna.ponto_eletronico.domain.service.util.timerecord

import br.com.santanna.ponto_eletronico.app.handler.model.BadRequestException
import br.com.santanna.ponto_eletronico.app.handler.model.UnauthorizedException
import br.com.santanna.ponto_eletronico.domain.dataprovider.EmployeeDataProvider
import br.com.santanna.ponto_eletronico.domain.dataprovider.TimeRecordDataProvider
import br.com.santanna.ponto_eletronico.domain.dto.timeRecord.DetailedTimeRecordDto
import br.com.santanna.ponto_eletronico.domain.dto.timeRecord.RecordCheckinDto
import br.com.santanna.ponto_eletronico.domain.dto.timeRecord.RecordCheckoutDto
import br.com.santanna.ponto_eletronico.domain.dto.timeRecord.UpdateTimeRecordDto
import br.com.santanna.ponto_eletronico.domain.entity.TimeRecord
import br.com.santanna.ponto_eletronico.domain.entity.employee.Employee
import br.com.santanna.ponto_eletronico.domain.entity.employee.EmployeeRole
import br.com.santanna.ponto_eletronico.infrastructure.security.jwt.JwtTokenUtil
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.abs
import kotlin.reflect.KMutableProperty1

private const val DATE_PATTERN = "dd-MM-yyyy"

private const val TIME_PATTERN = "HH:mm"
private const val EMPLOYEE_UNAUTHORIZED = "Colaborador sem permissão para o recurso"
@Component
class TimeRecordUtils(
    private val employeeDataProvider: EmployeeDataProvider,
    private val jwtTokenUtil: JwtTokenUtil
) {

    fun findTimeRecordsByDateRange(
        employeeId: UUID,
        startDate: LocalDate,
        endDate: LocalDate,
        timeRecordDataProvider: TimeRecordDataProvider
    ): List<TimeRecord> {
        val startDateTime = startDate.atStartOfDay()
        val endDateTime = endDate.atTime(23, 59, 59)
        return timeRecordDataProvider.findByEmployeeIdAndDateRange(employeeId, startDateTime, endDateTime)
    }


    fun findLastTimeRecord(employee: Employee?, timeRecordDataProvider: TimeRecordDataProvider): TimeRecord? {
        return timeRecordDataProvider.findTopByEmployeeAndEndWorkTimeIsNullOrderByStartWorkTimeDesc(employee)
    }

    fun createRecordCheckinDto(savedCheckin: TimeRecord): RecordCheckinDto {
        val formatter = DateTimeFormatter.ofPattern(DATE_PATTERN)
        return RecordCheckinDto(
            id = savedCheckin.id,
            startOfWorkTime = savedCheckin.startWorkTime?.format(DateTimeFormatter.ofPattern(TIME_PATTERN)),
            startOfWorkDate = savedCheckin.startWorkTime?.toLocalDate()?.format(formatter)
        )
    }

    fun createRecordCheckoutDto(savedCheckout: TimeRecord): RecordCheckoutDto {
        val formatter = DateTimeFormatter.ofPattern(DATE_PATTERN)
        return RecordCheckoutDto(
            id = savedCheckout.id,
            endWorkTime = savedCheckout.endWorkTime?.format(DateTimeFormatter.ofPattern(TIME_PATTERN)),
            timeWorked = formatTimeWorked(savedCheckout.timeWorked),
            endWorkDate = savedCheckout.endWorkTime?.toLocalDate()?.format(formatter)
        )
    }

    fun updateRecordFields(timeRecord: TimeRecord, updateTimeRecordDto: UpdateTimeRecordDto) {
        updateTimeRecordField(
            timeRecord,
            updateTimeRecordDto.startWorkDate,
            updateTimeRecordDto.startWorkTime,
            TimeRecord::startWorkTime,
            "start"
        )
        updateTimeRecordField(
            timeRecord,
            updateTimeRecordDto.endWorkDate,
            updateTimeRecordDto.endWorkTime,
            TimeRecord::endWorkTime,
            "end"
        )
    }

    private fun updateTimeRecordField(
        timeRecord: TimeRecord,
        newDate: String?,
        newTime: String?,
        dateTimeField: KMutableProperty1<TimeRecord, LocalDateTime?>,
        dateType: String
    ) {
        if (newDate != null && newTime != null) {
            val newDateTime = LocalDateTime.parse("$newDate $newTime", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
            validateDateChange(dateTimeField.get(timeRecord), newDateTime, dateType)
            dateTimeField.set(timeRecord, newDateTime)

            if (dateTimeField == TimeRecord::endWorkTime) {
                val duration = Duration.between(timeRecord.startWorkTime, newDateTime)
                timeRecord.timeWorked = duration.toMinutes()
            }
        }
    }

    private fun validateDateChange(currentDateTime: LocalDateTime?, newDateTime: LocalDateTime, dateType: String) {
        if (currentDateTime == null) {
            throw BadRequestException("$dateType Data nao pode ser vazia ")
        }

        if (currentDateTime.year != newDateTime.year) {
            throw BadRequestException("Ano nao pode ser alterado, deve permanecer o ano vigente")
        }

        if (currentDateTime.month != newDateTime.month) {
            throw BadRequestException("O mês deve ser o mesmo do registro que será alterado")
        }

        if (newDateTime.dayOfMonth > currentDateTime.dayOfMonth) {
            throw BadRequestException("O dia deve ser igual ou anterior ao do registro atual")
        }
    }

    fun formatBalance(balance: Long): String {
        val sign = if (balance < 0) "-" else ""
        val balanceHours = abs(balance / 60)
        val balanceRemainingMinutes = abs(balance % 60)
        return String.format("%s%02d:%02d", sign, balanceHours, balanceRemainingMinutes)
    }

    fun formatTimeWorked(timeWorkedMinutes: Long?): String? {
        return timeWorkedMinutes?.let {
            val hours = it / 60
            val minutes = it % 60
            String.format("%02d:%02d", hours, minutes)
        }
    }

    fun getCurrentUserId(): UUID {
        val authentication = SecurityContextHolder.getContext().authentication
        val token = authentication.credentials.toString()
        return jwtTokenUtil.getUserIdFromToken(token)
    }

    fun validateManager(password: String): Employee {
        val manager = validateManagerWithoutPassword()

        val isPasswordValid = BCryptPasswordEncoder().matches(password, manager.password)
        if (!isPasswordValid) {
            throw UnauthorizedException("Senha Inválida")
        }

        return manager
    }

    fun validateManagerWithoutPassword(): Employee {
        val id = getCurrentUserId()
        val manager = employeeDataProvider.findById(id)

        if (manager.role != EmployeeRole.ADMIN && manager.role != EmployeeRole.MANAGER) {
            throw UnauthorizedException(EMPLOYEE_UNAUTHORIZED)
        }

        return manager
    }

    fun validateSameCompanyById(employeeId: UUID, manager: Employee) {
        val employee = employeeDataProvider.findById(employeeId)
        if (employee.company?.id != manager.company?.id) {
            throw UnauthorizedException("Colaborador não está na empresa do gerente")
        }
    }

    fun convertToUpdateTimeRecordDto(savedUpdate: TimeRecord): UpdateTimeRecordDto {
        val formatter = DateTimeFormatter.ofPattern(DATE_PATTERN)
        return UpdateTimeRecordDto(
            id = savedUpdate.id,
            startWorkTime = savedUpdate.startWorkTime?.format(DateTimeFormatter.ofPattern(TIME_PATTERN)),
            startWorkDate = savedUpdate.startWorkTime?.toLocalDate()?.format(formatter),
            endWorkTime = savedUpdate.endWorkTime?.format(DateTimeFormatter.ofPattern(TIME_PATTERN)),
            endWorkDate = savedUpdate.endWorkTime?.toLocalDate()?.format(formatter)
        )
    }

    fun convertToDetailedTimeRecordDto(timeRecord: TimeRecord): DetailedTimeRecordDto {
        return DetailedTimeRecordDto(
            id = timeRecord.id,
            startWorkTime = timeRecord.startWorkTime?.format(DateTimeFormatter.ofPattern(TIME_PATTERN)),
            endWorkTime = timeRecord.endWorkTime?.format(DateTimeFormatter.ofPattern(TIME_PATTERN)),
            startWorkDate = timeRecord.startWorkTime?.toLocalDate()?.format(DateTimeFormatter.ISO_DATE),
            endWorkDate = timeRecord.endWorkTime?.toLocalDate()?.format(DateTimeFormatter.ISO_DATE),
            timeWorked = formatTimeWorked(timeRecord.timeWorked),
            edited = timeRecord.edited
        )
    }
}
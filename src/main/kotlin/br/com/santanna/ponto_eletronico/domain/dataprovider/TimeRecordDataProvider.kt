package br.com.santanna.ponto_eletronico.domain.dataprovider

import br.com.santanna.ponto_eletronico.domain.entity.employee.Employee
import br.com.santanna.ponto_eletronico.domain.entity.timerecord.TimeRecord
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime
import java.util.*

interface TimeRecordDataProvider {
    fun findTopByEmployeeAndEndWorkTimeIsNullOrderByStartWorkTimeDesc(employee: Employee?): TimeRecord?
    fun findByEmployeeIdAndDateRange(employeeId: UUID, startDate: LocalDateTime, endDate: LocalDateTime): List<TimeRecord>
    fun findByEmployeeIdAndDateRange(employeeId: UUID, startDate: LocalDateTime, endDate: LocalDateTime, pageable: Pageable): Page<TimeRecord>
    fun findById(id: Long): TimeRecord?
    fun save(timeRecord: TimeRecord): TimeRecord
    fun delete(timeRecord: TimeRecord)
}
package br.com.santanna.ponto_eletronico.domain.dataprovider

import br.com.santanna.ponto_eletronico.domain.entity.Employee
import br.com.santanna.ponto_eletronico.domain.entity.TimeRecord
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

interface TimeRecordDataProvider {
    fun findTopByEmployeeAndEndWorkTimeIsNullOrderByStartWorkTimeDesc(employee: Employee?): TimeRecord?
    fun findByEmployeeCpfAndDateRange(cpf: String, startDate: LocalDateTime, endDate: LocalDateTime): List<TimeRecord>
    fun findByEmployeeCpfAndDateRange(cpf: String, startDate: LocalDateTime, endDate: LocalDateTime, pageable: Pageable): Page<TimeRecord>
    fun findById(id: Long): TimeRecord?
    fun save(timeRecord: TimeRecord): TimeRecord
    fun delete(timeRecord: TimeRecord)
}
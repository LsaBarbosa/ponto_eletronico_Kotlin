package br.com.santanna.ponto_eletronico.infrastructure.repository

import br.com.santanna.ponto_eletronico.domain.entity.employee.Employee
import br.com.santanna.ponto_eletronico.domain.entity.timerecord.TimeRecord
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime
import java.util.*

interface TimeRecordRepository : JpaRepository<TimeRecord, Long> {

    fun findTopByEmployeeAndEndWorkTimeIsNullOrderByStartWorkTimeDesc(employee: Employee?): TimeRecord?

    @Query("SELECT t FROM TimeRecord t WHERE t.employee.id = :employeeId AND t.startWorkTime BETWEEN :startDate AND :endDate ORDER BY t.id ASC")
    fun findByEmployeeIdAndDateRange(
        @Param("employeeId") employeeId: UUID,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<TimeRecord>


    @Query("SELECT t FROM TimeRecord t WHERE t.employee.id = :employeeId AND t.startWorkTime BETWEEN :startDate AND :endDate ORDER BY t.id ASC")
    fun findByEmployeeIdAndDateRange(
        @Param("employeeId") employeeId: UUID,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime,
        pageable: Pageable
    ): Page<TimeRecord>


}
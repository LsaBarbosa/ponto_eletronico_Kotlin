package br.com.santanna.ponto_eletronico.repository

import br.com.santanna.ponto_eletronico.model.Employee
import br.com.santanna.ponto_eletronico.model.TimeRecord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface TimeRecordRepository : JpaRepository<TimeRecord, Long> {

    fun findTopByEmployeeAndEndWorkTimeIsNullOrderByStartWorkTimeDesc(employee: Employee): TimeRecord?

    @Query("SELECT t FROM TimeRecord t WHERE t.employee.name = :name AND  t.employee.surname = :surname AND t.startWorkTime BETWEEN :startDate AND :endDate")
    fun findByEmployeeNameAndDateRange(
        @Param("name") name: String,
        @Param("surname") surname: String,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<TimeRecord>

}
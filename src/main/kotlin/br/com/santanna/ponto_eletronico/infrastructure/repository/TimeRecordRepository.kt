package br.com.santanna.ponto_eletronico.infrastructure.repository

import br.com.santanna.ponto_eletronico.domain.entity.Employee
import br.com.santanna.ponto_eletronico.domain.entity.TimeRecord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface TimeRecordRepository : JpaRepository<TimeRecord, Long> {

    fun findTopByEmployeeAndEndWorkTimeIsNullOrderByStartWorkTimeDesc(employee: Employee?): TimeRecord?

    @Query("SELECT t FROM TimeRecord t WHERE t.employee.cpf = :cpf AND t.startWorkTime BETWEEN :startDate AND :endDate")
    fun findByEmployeeCpfAndDateRange(
        @Param("cpf") cpf: String,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<TimeRecord>

}
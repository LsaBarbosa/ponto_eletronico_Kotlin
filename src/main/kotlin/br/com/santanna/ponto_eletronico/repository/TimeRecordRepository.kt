package br.com.santanna.ponto_eletronico.repository

import br.com.santanna.ponto_eletronico.model.Employee
import br.com.santanna.ponto_eletronico.model.TimeRecord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface TimeRecordRepository : JpaRepository<TimeRecord, Long> {

    fun findTopByEmployeeAndEndWorkTimeIsNullOrderByStartWorkTimeDesc(employee: Employee): TimeRecord?
    fun findByEmployeeAndStartWorkTimeBetween(
        employee: Employee, startWorkTime: LocalDateTime, endWorkTime: LocalDateTime
    ): List<TimeRecord>?

    @Query("SELECT rp FROM TimeRecord rp WHERE rp.employee.name = :name AND rp.endWorkTime IS NULL")
    fun findRegistrationCheckInActive(@Param("name") name: String): TimeRecord
}
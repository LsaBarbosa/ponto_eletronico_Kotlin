package br.com.santanna.infrastructure.repository

import br.com.santanna.domain.entity.Employee
import br.com.santanna.infrastructure.model.TimeRecordModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface TimeRecordRepository : JpaRepository<TimeRecordModel, Long> {

    fun findTopByEmployeeAndEndWorkTimeIsNullOrderByStartWorkTimeDesc(employee: Employee?): TimeRecordModel?

    @Query("SELECT t FROM TimeRecordModel t WHERE t.employeeModel.name = :name AND  t.employeeModel.surname = :surname AND t.startWorkTime BETWEEN :startDate AND :endDate")
    fun findByEmployeeNameAndDateRange(
        @Param("name") name: String?,
        @Param("surname") surname: String?,
        @Param("startDate") startDate: LocalDateTime?,
        @Param("endDate") endDate: LocalDateTime?
    ): List<TimeRecordModel>

}
package br.com.santanna.domain.dataprovider

import br.com.santanna.domain.entity.Employee
import br.com.santanna.domain.entity.TimeRecord
import java.time.LocalDateTime

interface TimeRecordDataProvider {

    fun findTopByEmployeeAndEndWorkTimeIsNullOrderByStartWorkTimeDesc(employee: Employee?): TimeRecord?
    fun findByEmployeeNameAndDateRange(
        name: String?,
        surname: String?,
        startDate: LocalDateTime?,
        endDate: LocalDateTime?
    ): List<TimeRecord>

    fun save(timeRecord: TimeRecord): TimeRecord
    fun updateTimeRecordById(id: Long): TimeRecord

}
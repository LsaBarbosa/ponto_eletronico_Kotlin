package br.com.santanna.ponto_eletronico.repository.dataProvider

import br.com.santanna.ponto_eletronico.model.Employee
import br.com.santanna.ponto_eletronico.model.TimeRecord
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface TimeRecordDataProvider {

    fun findTopByEmployeeAndEndWorkTimeIsNullOrderByStartWorkTimeDesc(employee: Employee?):TimeRecord?
    fun findByEmployeeNameAndDateRange(@Param("name") name: String?,
                                       @Param("surname") surname: String?,
                                       @Param("startDate") startDate: LocalDateTime?,
                                       @Param("endDate") endDate: LocalDateTime?
    ): List<TimeRecord>

    fun save(timeRecord: TimeRecord):TimeRecord
    fun updateTimeRecordById(id:Long ):TimeRecord

}
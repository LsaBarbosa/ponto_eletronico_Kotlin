package br.com.santanna.ponto_eletronico.domain.dataprovider

import br.com.santanna.ponto_eletronico.domain.entity.Employee
import br.com.santanna.ponto_eletronico.domain.entity.TimeRecord
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface TimeRecordDataProvider {

    fun findTopByEmployeeAndEndWorkTimeIsNullOrderByStartWorkTimeDesc(employee: Employee?): TimeRecord?
    fun findByEmployeeNameAndDateRange(@Param("name") name: String?,
                                       @Param("surname") surname: String?,
                                       @Param("startDate") startDate: LocalDateTime?,
                                       @Param("endDate") endDate: LocalDateTime?
    ): List<TimeRecord>

    fun save(timeRecord: TimeRecord): TimeRecord
    fun updateTimeRecordById(id:Long ): TimeRecord

}
package br.com.santanna.infrastructure.impl

import br.com.santanna.domain.entity.Employee
import br.com.santanna.domain.entity.TimeRecord
import br.com.santanna.infrastructure.exception.model.ObjectNotFoundException
import br.com.santanna.infrastructure.model.TimeRecordModel
import br.com.santanna.infrastructure.model.toTimeRecord
import br.com.santanna.infrastructure.repository.TimeRecordRepository
import br.com.santanna.domain.dataprovider.TimeRecordDataProvider
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TimeRecordDataProviderImpl(private val timeRecordRepository: TimeRecordRepository) : TimeRecordDataProvider {

    override fun findTopByEmployeeAndEndWorkTimeIsNullOrderByStartWorkTimeDesc(employee: Employee?): TimeRecord? {
        val employee = timeRecordRepository.findTopByEmployeeAndEndWorkTimeIsNullOrderByStartWorkTimeDesc(employee)
        return employee?.toTimeRecord()
    }

    override fun findByEmployeeNameAndDateRange(name: String?, surname: String?, startDate: LocalDateTime?, endDate: LocalDateTime?
    ): List<TimeRecord> {
    val employee= timeRecordRepository.findByEmployeeNameAndDateRange(name,surname,startDate,endDate)
        return employee.map { it.toTimeRecord() }
    }

    override fun save(timeRecord: TimeRecord): TimeRecord {
      val saveTimeRecord = timeRecordRepository.save(timeRecord.TimeRecordModel())
        return saveTimeRecord.toTimeRecord()
    }

    override fun updateTimeRecordById(id: Long): TimeRecord {
        val timeRecord = timeRecordRepository.findById(id)
            .orElseThrow { ObjectNotFoundException("Registro de hora não encontrado") }
        return timeRecord.toTimeRecord()
    }
}
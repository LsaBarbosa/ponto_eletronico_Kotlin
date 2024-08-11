package br.com.santanna.ponto_eletronico.infrastructure.dataProvider.impl

import br.com.santanna.ponto_eletronico.domain.dataprovider.TimeRecordDataProvider
import br.com.santanna.ponto_eletronico.domain.entity.employee.Employee
import br.com.santanna.ponto_eletronico.domain.entity.TimeRecord
import br.com.santanna.ponto_eletronico.infrastructure.repository.TimeRecordRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class TimeRecordDataProviderImpl(private val timeRecordRepository: TimeRecordRepository) : TimeRecordDataProvider {

    override fun findTopByEmployeeAndEndWorkTimeIsNullOrderByStartWorkTimeDesc(employee: Employee?): TimeRecord? {
        return timeRecordRepository.findTopByEmployeeAndEndWorkTimeIsNullOrderByStartWorkTimeDesc(employee)
    }

    override fun findByEmployeeIdAndDateRange(employeeId: UUID, startDate: LocalDateTime, endDate: LocalDateTime): List<TimeRecord> {
        return timeRecordRepository.findByEmployeeIdAndDateRange(employeeId, startDate, endDate)
    }


    override fun findByEmployeeIdAndDateRange(employeeId: UUID, startDate: LocalDateTime, endDate: LocalDateTime, pageable: Pageable): Page<TimeRecord> {
        return timeRecordRepository.findByEmployeeIdAndDateRange(employeeId, startDate, endDate, pageable)
    }

    override fun save(timeRecord: TimeRecord): TimeRecord {
        return timeRecordRepository.save(timeRecord)
    }


    override fun findById(id: Long): TimeRecord? {
        return timeRecordRepository.findById(id).orElse(null)
    }

    override fun delete(timeRecord: TimeRecord) {
        timeRecordRepository.delete(timeRecord)
    }
}
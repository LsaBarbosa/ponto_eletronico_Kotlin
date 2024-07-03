package br.com.santanna.ponto_eletronico.infrastructure.dataProvider.impl

import br.com.santanna.ponto_eletronico.domain.dataprovider.TimeRecordDataProvider
import br.com.santanna.ponto_eletronico.domain.entity.Employee
import br.com.santanna.ponto_eletronico.domain.entity.TimeRecord
import br.com.santanna.ponto_eletronico.infrastructure.repository.TimeRecordRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TimeRecordDataProviderImpl(private val timeRecordRepository: TimeRecordRepository) : TimeRecordDataProvider {

    override fun findTopByEmployeeAndEndWorkTimeIsNullOrderByStartWorkTimeDesc(employee: Employee?): TimeRecord? {
        return timeRecordRepository.findTopByEmployeeAndEndWorkTimeIsNullOrderByStartWorkTimeDesc(employee)
    }

    override fun findByEmployeeCpfAndDateRange(cpf: String, startDate: LocalDateTime, endDate: LocalDateTime): List<TimeRecord> {
        return timeRecordRepository.findByEmployeeCpfAndDateRange(cpf, startDate, endDate)
    }

    override fun save(timeRecord: TimeRecord): TimeRecord {
        return timeRecordRepository.save(timeRecord)
    }


    override fun findById(id: Long): TimeRecord? {
        return timeRecordRepository.findById(id).orElse(null)
    }
}
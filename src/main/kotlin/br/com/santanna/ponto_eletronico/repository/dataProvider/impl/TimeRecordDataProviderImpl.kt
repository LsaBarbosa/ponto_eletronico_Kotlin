package br.com.santanna.ponto_eletronico.repository.dataProvider.impl

import br.com.santanna.ponto_eletronico.model.Employee
import br.com.santanna.ponto_eletronico.model.TimeRecord
import br.com.santanna.ponto_eletronico.repository.TimeRecordRepository
import br.com.santanna.ponto_eletronico.repository.dataProvider.TimeRecordDataProvider
import br.com.santanna.ponto_eletronico.service.exception.DataIntegrityViolationException
import br.com.santanna.ponto_eletronico.service.exception.ObjectNotFoundException
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TimeRecordDataProviderImpl(private val timeRecordRepository: TimeRecordRepository) : TimeRecordDataProvider {

    override fun findTopByEmployeeAndEndWorkTimeIsNullOrderByStartWorkTimeDesc(employee: Employee?): TimeRecord? {
        return timeRecordRepository.findTopByEmployeeAndEndWorkTimeIsNullOrderByStartWorkTimeDesc(employee)
    }

    override fun findByEmployeeNameAndDateRange(name: String?, surname: String?, startDate: LocalDateTime?, endDate: LocalDateTime?
    ): List<TimeRecord> {
       return timeRecordRepository.findByEmployeeNameAndDateRange(name,surname,startDate,endDate)
    }

    override fun save(timeRecord: TimeRecord): TimeRecord {
        return timeRecordRepository.save(timeRecord)
    }

    override fun updateTimeRecordById(id: Long): TimeRecord {
        val timeRecord = timeRecordRepository.findById(id)
            .orElseThrow { ObjectNotFoundException("Registro de hora não encontrado") }
        return timeRecord
    }
}
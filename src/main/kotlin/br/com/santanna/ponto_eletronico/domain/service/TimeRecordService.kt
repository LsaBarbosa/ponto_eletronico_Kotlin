package br.com.santanna.ponto_eletronico.domain.service

import br.com.santanna.ponto_eletronico.domain.dto.timeRecord.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate

interface TimeRecordService {
    fun registerCheckin(cpf: String): RecordCheckinDto?
    fun registerCheckout(cpf: String): RecordCheckoutDto?
    fun updateTimeRecord(cpf:String,updateTimeRecordDto: UpdateTimeRecordDto): UpdateTimeRecordDto
    fun balanceHoursByDate(cpf: String, startDate: LocalDate, endDate: LocalDate): BalanceHoursDto
    fun getTimeRecordsByEmployeeCpfAndDateRange(cpf: String, startDate: LocalDate, endDate: LocalDate): List<DetailedTimeRecordDto>
    fun getTimeRecordsByEmployeeCpfAndDateRange(cpf: String, startDate: LocalDate, endDate: LocalDate, pageable: Pageable): Page<DetailedTimeRecordDto>
    fun deleteTimeRecord(cpf: String, timeRecordId: Long)
}
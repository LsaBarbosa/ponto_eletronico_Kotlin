package br.com.santanna.ponto_eletronico.domain.service

import br.com.santanna.ponto_eletronico.domain.dto.timeRecord.*
import java.time.LocalDate

interface TimeRecordService {
    fun registerCheckin(cpf: String): RecordCheckinDto?
    fun registerCheckout(cpf: String): RecordCheckoutDto?
    fun updateTimeRecord(cpf:String,updateTimeRecordDto: UpdateTimeRecordDto): UpdateTimeRecordDto
    fun overtimeByDate(cpf: String, startDate: LocalDate, endDate: LocalDate): OvertimeDto
    fun getTimeRecordsByEmployeeCpfAndDateRange(cpf: String, startDate: LocalDate, endDate: LocalDate): List<DetailedTimeRecordDto>
    fun deleteTimeRecord(cpf: String, timeRecordId: Long)
}
package br.com.santanna.ponto_eletronico.service

import br.com.santanna.ponto_eletronico.model.dto.timeRecord.*
import java.time.LocalDate

interface TimeRecordService {
    fun registerCheckin(name: String, surname: String): RecordCheckinDto?
    fun registerCheckout(name: String, surname: String): RecordCheckoutDto?
    fun updateTimeRecord(updateTimeRecordDto: UpdateTimeRecordDto): UpdateTimeRecordDto
    fun overtimeByDate(name: String, surname: String, startDate: LocalDate, endDate: LocalDate): OvertimeDto
    fun getTimeRecordsByEmployeeNameAndDateRange(name: String, surname: String, startDate: LocalDate, endDate: LocalDate): List<DetailedTimeRecordDto>
}
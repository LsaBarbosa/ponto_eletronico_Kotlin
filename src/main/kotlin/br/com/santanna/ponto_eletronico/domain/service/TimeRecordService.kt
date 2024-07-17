package br.com.santanna.ponto_eletronico.domain.service

import br.com.santanna.ponto_eletronico.domain.dto.timeRecord.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate

interface TimeRecordService {
    fun registerCheckin(): RecordCheckinDto?
    fun registerCheckout(): RecordCheckoutDto?
    fun updateTimeRecord(updateTimeRecordRequestDto: UpdateTimeRecordRequestDto): UpdateTimeRecordDto
    fun balanceHoursByDateForManager(searchRequestDto: SearchByDateTimeRecordRequestDto): BalanceHoursDto
    fun balanceHoursByDate(  startDate: LocalDate, endDate: LocalDate): BalanceHoursDto
    fun getTimeRecordsByEmployeeCpfAndDateRangePageable( startDate: LocalDate, endDate: LocalDate, pageable: Pageable): Page<DetailedTimeRecordDto>
    fun getTimeRecordsByEmployeeCpfAndDateRangePageableForManager(searchRequestDto: SearchByDateTimeRecordRequestDto, pageable: Pageable): Page<DetailedTimeRecordDto>
    fun getTimeRecordsByEmployeeCpfAndDateRangePageableForPDF(cpf: String, startDate: LocalDate, endDate: LocalDate, pageable: Pageable): Page<DetailedTimeRecordDto>
    fun deleteTimeRecord(deleteTimeRecordRequestDto: DeleteTimeRecordRequestDto)
    }
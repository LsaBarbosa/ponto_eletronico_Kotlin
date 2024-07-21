package br.com.santanna.ponto_eletronico.domain.service

import br.com.santanna.ponto_eletronico.domain.dto.timeRecord.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate

interface TimeRecordService {
    fun registerCheckin(): RecordCheckinDto?
    fun registerCheckout(): RecordCheckoutDto?
    fun balanceHoursByDate(  startDate: LocalDate, endDate: LocalDate): BalanceHoursDto
    fun getTimeRecordsByEmployeeCpfAndDateRangePageable( searchByDateTimeRecordDto:SearchByDateTimeRecordDto, pageable: Pageable): Page<DetailedTimeRecordDto>
    fun updateTimeRecordAsManager(updateTimeRecordRequestDto: UpdateTimeRecordRequestDto): UpdateTimeRecordDto
    fun balanceHoursByDateAsManager(searchRequestDto: SearchByDateTimeRecordRequestDto): BalanceHoursDto
    fun getTimeRecordsByEmployeeCpfAndDateRangePageableAsManager(searchRequestDto: SearchByDateTimeRecordRequestDto, pageable: Pageable): Page<DetailedTimeRecordDto>
    fun getTimeRecordsByEmployeeCpfAndDateRangePageableToPDFAsManager(cpf: String, startDate: LocalDate, endDate: LocalDate, pageable: Pageable): Page<DetailedTimeRecordDto>
    fun deleteTimeRecordAsManager(deleteTimeRecordRequestDto: DeleteTimeRecordRequestDto)
    }
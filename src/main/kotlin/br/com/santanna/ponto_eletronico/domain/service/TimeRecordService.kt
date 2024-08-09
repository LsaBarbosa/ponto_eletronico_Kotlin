package br.com.santanna.ponto_eletronico.domain.service

import br.com.santanna.ponto_eletronico.domain.dto.timeRecord.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate
import java.util.*

interface TimeRecordService {
    fun registerCheckin(): RecordCheckinDto?
    fun registerCheckout(): RecordCheckoutDto?
    fun balanceHoursByDate(  searchByDateTimeRecordDto:SearchByDateTimeRecordDto): BalanceHoursDto
    fun getTimeRecordsByEmployeeIdAndDateRangePageable(searchByDateTimeRecordDto: SearchByDateTimeRecordDto, pageable: Pageable): Page<DetailedTimeRecordDto>
    fun updateTimeRecordAsManager(updateTimeRecordRequestDto: UpdateTimeRecordRequestDto): UpdateTimeRecordDto
    fun balanceHoursByDateAsManager(searchRequestDto: SearchByDateTimeRecordRequestDto): BalanceHoursDto
    fun getTimeRecordsByEmployeeIdAndDateRangePageableAsManager(searchRequestDto: SearchByDateTimeRecordRequestDto, pageable: Pageable): Page<DetailedTimeRecordDto>
    fun getTimeRecordsByEmployeeIdAndDateRangePageableToPDFAsManager(employeeId: UUID, startDate: LocalDate, endDate: LocalDate, pageable: Pageable): Page<DetailedTimeRecordDto>
    fun deleteTimeRecordAsManager(deleteTimeRecordRequestDto: DeleteTimeRecordRequestDto)
    }
package br.com.santanna.ponto_eletronico.domain.dto.timeRecord

import java.util.*

data class SearchByDateTimeRecordRequestDto(
    val employeeIdTarget: UUID,
    val passwords: String,
    val searchByDateTimeRecordDto: SearchByDateTimeRecordDto
)

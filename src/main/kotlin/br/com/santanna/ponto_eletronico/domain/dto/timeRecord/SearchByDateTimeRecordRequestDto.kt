package br.com.santanna.ponto_eletronico.domain.dto.timeRecord

data class SearchByDateTimeRecordRequestDto(

    val employeeCpfTarget: String,
    val passwords: String,
    val searchByDateTimeRecordDto: SearchByDateTimeRecordDto
)

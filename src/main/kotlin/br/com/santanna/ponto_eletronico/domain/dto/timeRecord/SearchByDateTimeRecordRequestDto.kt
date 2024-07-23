package br.com.santanna.ponto_eletronico.domain.dto.timeRecord

import org.hibernate.validator.constraints.br.CPF

data class SearchByDateTimeRecordRequestDto(
    @field:CPF
    val employeeCpfTarget: String,
    val passwords: String,
    val searchByDateTimeRecordDto: SearchByDateTimeRecordDto
)

package br.com.santanna.ponto_eletronico.domain.dto.timeRecord

import org.hibernate.validator.constraints.br.CPF

data class DeleteTimeRecordRequestDto(
    val timeRecordId: Long,
    @field:CPF
    val employeeCpfTarget: String,

    val passwords: String
)

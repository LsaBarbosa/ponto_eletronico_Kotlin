package br.com.santanna.ponto_eletronico.domain.dto.timeRecord

data class DeleteTimeRecordRequestDto(
    val timeRecordId: Long,
    val employeeCpfTarget: String,

    val passwords: String
)

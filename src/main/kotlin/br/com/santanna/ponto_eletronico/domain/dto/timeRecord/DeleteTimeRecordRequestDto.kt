package br.com.santanna.ponto_eletronico.domain.dto.timeRecord

data class DeleteTimeRecordRequestDto(
    val cpf: String,
    val passwords: String
)

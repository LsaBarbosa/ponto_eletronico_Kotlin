package br.com.santanna.ponto_eletronico.domain.dto.employee

data class DeleteEmployeeRequestDto(
    val cpf: String,
    val passwords: String
)

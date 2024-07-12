package br.com.santanna.ponto_eletronico.domain.dto.company

data class DeleteCompanyRequestDto(
    val companyCNPJ: String,
    val employeeCpf: String,
    val passwords: String
)

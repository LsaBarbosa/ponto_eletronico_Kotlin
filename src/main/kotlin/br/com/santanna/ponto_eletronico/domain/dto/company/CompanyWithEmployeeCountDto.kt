package br.com.santanna.ponto_eletronico.domain.dto.company

data class CompanyWithEmployeeCountDto(
    val nameCompany: String?,
    val companyCNPJ: String?,
    val employeeCount: Long?
)

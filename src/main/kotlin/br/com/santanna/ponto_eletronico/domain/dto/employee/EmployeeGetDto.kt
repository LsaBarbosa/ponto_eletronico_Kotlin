package br.com.santanna.ponto_eletronico.domain.dto.employee

import br.com.santanna.ponto_eletronico.domain.dto.company.CompanyGetDto

data class EmployeeGetDto(
    var id: Long? = null,
    var name: String? = null,
    var surname: String? = null,
    var salary: Double? = null,
    var position: String? = null,
    var cpf: String? = null,

    val company: CompanyGetDto? = null
)



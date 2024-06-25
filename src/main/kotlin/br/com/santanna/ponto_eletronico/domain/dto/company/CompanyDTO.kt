package br.com.santanna.ponto_eletronico.domain.dto.company

import br.com.santanna.ponto_eletronico.domain.dto.employee.SimpleEmployeeDto

data class CompanyDTO(
    var id: Long? = null,
    var nameCompany:String? = null,
    var companyCNPJ: String? = null,
    val employees: List<SimpleEmployeeDto>? = ArrayList(),
)

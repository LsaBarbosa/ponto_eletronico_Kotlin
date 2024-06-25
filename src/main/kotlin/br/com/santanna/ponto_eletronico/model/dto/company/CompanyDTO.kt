package br.com.santanna.ponto_eletronico.model.dto.company

import br.com.santanna.ponto_eletronico.model.dto.employee.SimpleEmployeeDto

data class CompanyDTO(
    var id: Long? = null,
    var nameCompany:String? = null,
    var companyCNPJ: String? = null,
    val employees: List<SimpleEmployeeDto>? = ArrayList(),
)

package br.com.santanna.domain.dto.company

import br.com.santanna.domain.dto.employee.SimpleEmployeeDto

data class CompanyDTO(
    var id: Long? = null,
    var nameCompany:String? = null,
    var companyCNPJ: String? = null,
    val employees: List<SimpleEmployeeDto>? = ArrayList(),
)

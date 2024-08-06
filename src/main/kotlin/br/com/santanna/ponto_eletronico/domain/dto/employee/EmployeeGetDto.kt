package br.com.santanna.ponto_eletronico.domain.dto.employee

import br.com.santanna.ponto_eletronico.domain.dto.company.CompanyGetDto
import br.com.santanna.ponto_eletronico.domain.entity.employee.EmployeeRole
import java.util.UUID

data class EmployeeGetDto(
    var id:UUID? = null,
    var name: String? = null,
    var surname: String? = null,
    var salary: Double? = null,
    var position: String? = null,
    var cpf: String? = null,
    var role: EmployeeRole? = null,
    var email: String? = null,
    val company: CompanyGetDto? = null
)



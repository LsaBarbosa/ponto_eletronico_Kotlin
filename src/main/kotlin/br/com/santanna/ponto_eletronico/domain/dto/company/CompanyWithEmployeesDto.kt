package br.com.santanna.ponto_eletronico.domain.dto.company

import br.com.santanna.ponto_eletronico.domain.dto.employee.EmployeeDto

data class CompanyWithEmployeesDto(
    val id: Long?,
    val nameCompany: String?,
    val companyCNPJ: String?,
    val employees: List<EmployeeDto>
)

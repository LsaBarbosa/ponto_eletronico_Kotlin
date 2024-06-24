package br.com.santanna.ponto_eletronico.model.dto.company

import br.com.santanna.ponto_eletronico.model.dto.employee.EmployeeDto

data class CompanyWithEmployeesDto(
    val id: Long?,
    val nameCompany: String?,
    val companyCNPJ: String?,
    val employees: List<EmployeeDto>
)

package br.com.santanna.domain.dto.company

import br.com.santanna.domain.dto.employee.EmployeeDto

data class CompanyWithEmployeesDto(
    val id: Long?,
    val nameCompany: String?,
    val companyCNPJ: String?,
    val employees: List<EmployeeDto>
)

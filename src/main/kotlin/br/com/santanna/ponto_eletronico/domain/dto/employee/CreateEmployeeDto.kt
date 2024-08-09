package br.com.santanna.ponto_eletronico.domain.dto.employee

data class CreateEmployeeDto(

    val passwordsManager: String,
    val employeeDto: EmployeeDto?
)

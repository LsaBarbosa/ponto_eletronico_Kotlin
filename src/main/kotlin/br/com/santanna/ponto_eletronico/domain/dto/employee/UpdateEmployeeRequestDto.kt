package br.com.santanna.ponto_eletronico.domain.dto.employee

data class UpdateEmployeeRequestDto(
    val employeeCpfTarget: String,

    val passwords: String,
    val updateEmployeeDto: UpdateEmployeeDto
)

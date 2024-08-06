package br.com.santanna.ponto_eletronico.domain.dto.employee

import java.util.*

data class UpdateEmployeeRequestDto(
    val employeeId: UUID,
    val updateEmployeeDto: UpdateEmployeeDto
)

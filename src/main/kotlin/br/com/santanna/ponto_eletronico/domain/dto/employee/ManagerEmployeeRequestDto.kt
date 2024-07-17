package br.com.santanna.ponto_eletronico.domain.dto.employee

import jakarta.validation.constraints.NotBlank

data class ManagerEmployeeRequestDto(
    @field:NotBlank(message = "A senha do gerente não pode estar em branco")
    val passwords: String
)

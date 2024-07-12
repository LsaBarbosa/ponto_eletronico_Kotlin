package br.com.santanna.ponto_eletronico.domain.dto.employee

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ManagerEmployeeRequestDto(
    @field:NotBlank(message = "O CPF do gerente não pode estar em branco")
    @field:Size(min = 11, max = 11, message = "O CPF do gerente deve ter exatamente 11 caracteres")
    val managerCpf: String,
    @field:NotBlank(message = "A senha do gerente não pode estar em branco")
    val passwords: String
)

package br.com.santanna.ponto_eletronico.domain.dto.company

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.br.CNPJ
import org.hibernate.validator.constraints.br.CPF

data class DeleteCompanyRequestDto(
    @field:Size(min = 14, max = 14, message = "O CNPJ deve ter exatamente 14 caracteres")
    @field:CNPJ(message = "CNPJ inválido")
    var companyCNPJ: String,
    @field:Size(min = 11, max = 11, message = "O CPF do gerente deve ter exatamente 11 caracteres")
    @field:CPF(message = "CPF inválido")
    var employeeCpf: String,
    @field:NotBlank(message = "A senha do gerente não pode estar em branco")
    @field:Size(min = 8, max = 50, message = "A senha do gerente deve ter no mínimo 8 caracteres")
    val passwords: String
)

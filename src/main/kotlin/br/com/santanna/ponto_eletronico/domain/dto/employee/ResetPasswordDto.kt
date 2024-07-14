package br.com.santanna.ponto_eletronico.domain.dto.employee

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ResetPasswordDto(    @field:NotBlank(message = "O CPF do funcionário não pode estar em branco")
                                @field:Size(min = 11, max = 11, message = "O CPF do funcionário deve ter exatamente 11 caracteres")
                                val cpf: String,

                                @field:NotBlank(message = "O e-mail não pode estar em branco")
                                @field:Email(message = "E-mail inválido")
                                val email: String
)
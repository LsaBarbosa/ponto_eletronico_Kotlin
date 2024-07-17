package br.com.santanna.ponto_eletronico.domain.dto.employee

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdatePassword(


    @field:NotBlank(message = "A senha antiga não pode estar em branco")
    val oldPassword: String,

    @field:NotBlank(message = "A nova senha não pode estar em branco")
    @field:Size(min = 8, max = 50, message = "A nova senha deve ter no mínimo 8 caracteres")
    val newPassword: String,

    @field:NotBlank(message = "A confirmação da nova senha não pode estar em branco")
    val confirmPassword: String
)

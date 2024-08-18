package br.com.santanna.ponto_eletronico.domain.dto.employee

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class UpdateEmail(

    @field:NotBlank(message = "A novo email não pode estar em branco")
    @field:Email(message = "O email deve estar no formato correto: email@email.com")
    val newEmail: String
)

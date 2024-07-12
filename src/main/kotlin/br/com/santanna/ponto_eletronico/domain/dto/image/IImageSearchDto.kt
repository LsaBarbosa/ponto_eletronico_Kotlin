package br.com.santanna.ponto_eletronico.domain.dto.image

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ImageSearchDto  (
    @field:NotBlank(message = "O CPF do funcionário não pode estar em branco")
    @field:Size(min = 11, max = 11, message = "O CPF do funcionário deve ter exatamente 11 caracteres")
    val cpf: String,
    @field:NotBlank(message = "A senha não pode estar em branco")
    val passwords: String,
    @field:NotBlank(message = "A data de início não pode estar em branco")
    val startDate: String,
    @field:NotBlank(message = "A data de fim não pode estar em branco")
    val endDate: String
)
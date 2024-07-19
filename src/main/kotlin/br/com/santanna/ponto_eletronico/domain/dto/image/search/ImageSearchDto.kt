package br.com.santanna.ponto_eletronico.domain.dto.image.search

import jakarta.validation.constraints.NotBlank

data class ImageSearchDto(
    @field:NotBlank(message = "A data de início não pode estar em branco")
    val startDate: String,
    @field:NotBlank(message = "A data de fim não pode estar em branco")
    val endDate: String
)
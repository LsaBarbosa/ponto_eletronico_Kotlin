package br.com.santanna.ponto_eletronico.domain.dto.image

import org.hibernate.validator.constraints.br.CPF

data class DeleteImageRequestDto(
    val imageId: Long,
    @field:CPF
    val employeeCpfTarget: String,
    val passwords: String
)

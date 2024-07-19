package br.com.santanna.ponto_eletronico.domain.dto.image.search

import java.time.LocalDate

data class ImageListDto(
    val id: Long?,
    val name: String?,
    val surname: String?,
    val cpf: String?,
    val filePath: String?,
    val message: String?,
    val uploadDate: LocalDate?
)

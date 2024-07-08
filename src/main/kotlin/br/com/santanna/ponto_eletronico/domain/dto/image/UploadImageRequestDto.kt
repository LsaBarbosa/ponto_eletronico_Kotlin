package br.com.santanna.ponto_eletronico.domain.dto.image

import org.springframework.web.multipart.MultipartFile

data class UploadImageRequestDto(
    val cpf: String,
    val file: MultipartFile,
    val message: String?,
    val passwords: String

)

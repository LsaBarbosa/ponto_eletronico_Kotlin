package br.com.santanna.ponto_eletronico.domain.dto.image.update

import org.springframework.web.multipart.MultipartFile

data class UploadImageRequestDto(


    val file: MultipartFile,
    val message: String?

)

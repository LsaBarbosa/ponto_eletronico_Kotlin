package br.com.santanna.ponto_eletronico.domain.dto.image

data class UpdateImageRequestDto(   val imageId: Long,
                                    val employeeCpfTarget: String,
                                    val passwords: String,
                                    val updateImageMessageDto: UpdateImageMessageDto)

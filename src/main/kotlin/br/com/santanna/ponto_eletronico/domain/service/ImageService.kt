package br.com.santanna.ponto_eletronico.domain.service

import br.com.santanna.ponto_eletronico.domain.dto.image.DeleteImageRequestDto
import br.com.santanna.ponto_eletronico.domain.dto.image.ImageListDto
import br.com.santanna.ponto_eletronico.domain.dto.image.UpdateImageRequestDto
import br.com.santanna.ponto_eletronico.domain.dto.image.UploadImageRequestDto
import br.com.santanna.ponto_eletronico.domain.entity.Image

interface ImageService {
    fun storeImage(uploadImageRequestDto: UploadImageRequestDto): Image
    fun getImageByEmployeeCpf(cpf: String): ByteArray
    fun getImagesByEmployeeCpf(cpf: String): List<ImageListDto>
    fun getImageById(id: Long): ByteArray
    fun deleteImageById(deleteImageRequestDto: DeleteImageRequestDto)
    fun updateImageMessage(updateImageRequestDto: UpdateImageRequestDto): ImageListDto
}
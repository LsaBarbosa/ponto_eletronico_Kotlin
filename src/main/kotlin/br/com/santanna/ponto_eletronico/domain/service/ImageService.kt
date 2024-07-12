package br.com.santanna.ponto_eletronico.domain.service

import br.com.santanna.ponto_eletronico.domain.dto.image.*
import br.com.santanna.ponto_eletronico.domain.entity.Image

interface ImageService {
    fun storeImage(uploadImageRequestDto: UploadImageRequestDto): Image
    fun getImageByEmployeeCpf(cpf: String): ByteArray
    fun getImagesByEmployeeCpf(cpf: String): List<ImageListDto>
    fun getImageById(id: Long): ByteArray
    fun deleteImageById(deleteImageRequestDto: DeleteImageRequestDto)
    fun updateImageMessage(updateImageRequestDto: UpdateImageRequestDto): ImageListDto
    fun getImagesByEmployeeCpfAndDateRange(imageSearchDto: ImageSearchDto): List<ImageListDto>
}
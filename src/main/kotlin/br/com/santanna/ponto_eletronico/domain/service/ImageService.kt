package br.com.santanna.ponto_eletronico.domain.service

import br.com.santanna.ponto_eletronico.domain.dto.image.ImageListDto
import br.com.santanna.ponto_eletronico.domain.entity.Image
import org.springframework.web.multipart.MultipartFile

interface ImageService {
    fun storeImage(cpf: String, file: MultipartFile, message: String?): Image
    fun getImageByEmployeeCpf(cpf: String): ByteArray
    fun getImagesByEmployeeCpf(cpf: String): List<ImageListDto>
    fun getImageById(id: Long): ByteArray
    fun deleteImageById(id: Long)
}
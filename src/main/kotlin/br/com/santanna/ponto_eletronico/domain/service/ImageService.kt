package br.com.santanna.ponto_eletronico.domain.service

import br.com.santanna.ponto_eletronico.domain.entity.Image
import org.springframework.web.multipart.MultipartFile

interface ImageService {
    fun storeImage(cpf: String, file: MultipartFile): Image
    fun getImageByEmployeeCpf(cpf: String): ByteArray
}
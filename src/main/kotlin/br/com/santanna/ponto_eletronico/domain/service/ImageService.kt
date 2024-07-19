package br.com.santanna.ponto_eletronico.domain.service

import br.com.santanna.ponto_eletronico.domain.dto.image.manager.ManagerImageRequestDto
import br.com.santanna.ponto_eletronico.domain.dto.image.search.ImageListDto
import br.com.santanna.ponto_eletronico.domain.dto.image.search.ImageSearchDto
import br.com.santanna.ponto_eletronico.domain.dto.image.update.UpdateImageMessageDto
import br.com.santanna.ponto_eletronico.domain.dto.image.update.UploadImageRequestDto
import br.com.santanna.ponto_eletronico.domain.entity.Image

interface ImageService {
    fun storeImage(uploadImageRequestDto: UploadImageRequestDto): Image
    fun getImagesForCurrentUser(imageSearchDto: ImageSearchDto): List<ImageListDto>
    fun getImageByIdForCurrentUser(imageId: Long): ByteArray
    fun deleteImageByIdForCurrentUser(imageId: Long)
    fun updateImageMessageForCurrentUser(imageId: Long, updateImageMessageDto: UpdateImageMessageDto): ImageListDto
    fun getImagesByEmployeeCpfAsManager(managerImageRequestDto: ManagerImageRequestDto, imageSearchDto: ImageSearchDto): List<ImageListDto>
    fun getImageByIdAsManager(managerImageRequestDto: ManagerImageRequestDto): ByteArray
    fun deleteImageByIdAsManager(managerImageRequestDto: ManagerImageRequestDto)
    fun updateImageMessageAsManager(managerImageRequestDto: ManagerImageRequestDto): ImageListDto



}
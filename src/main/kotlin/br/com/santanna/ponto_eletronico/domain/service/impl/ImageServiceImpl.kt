package br.com.santanna.ponto_eletronico.domain.service.impl

import br.com.santanna.ponto_eletronico.app.handler.model.DataIntegrityViolationException
import br.com.santanna.ponto_eletronico.app.handler.model.ObjectNotFoundException
import br.com.santanna.ponto_eletronico.domain.dataprovider.EmployeeDataProvider
import br.com.santanna.ponto_eletronico.domain.dataprovider.ImageDataProvider
import br.com.santanna.ponto_eletronico.domain.dto.image.manager.ManagerImageRequestDto
import br.com.santanna.ponto_eletronico.domain.dto.image.search.ImageListDto
import br.com.santanna.ponto_eletronico.domain.dto.image.search.ImageSearchDto
import br.com.santanna.ponto_eletronico.domain.dto.image.update.UpdateImageMessageDto
import br.com.santanna.ponto_eletronico.domain.dto.image.update.UploadImageRequestDto
import br.com.santanna.ponto_eletronico.domain.entity.Image
import br.com.santanna.ponto_eletronico.domain.service.ImageService
import br.com.santanna.ponto_eletronico.domain.service.util.image.ImageServiceUtils
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDate

private const val NO_IMAGE_FOUND_WITH_ID_ = "Nenhuma imagem encontrada para o ID: "

@Service
class ImageServiceImpl(

    private val imageDataProvider: ImageDataProvider,
    private val employeeDataProvider: EmployeeDataProvider,
    private val imageServiceUtils: ImageServiceUtils,

    ) : ImageService {

    override fun getImageByIdForCurrentUser(imageId: Long): ByteArray {
        val userId = imageServiceUtils.getCurrentUserId()
        val image = imageDataProvider.findByIdAndEmployeeId(imageId, userId)
            ?: throw ObjectNotFoundException("$NO_IMAGE_FOUND_WITH_ID_: $userId")
        return imageServiceUtils.loadFileAsResource(image.filePath!!)
    }

    override fun getImagesForCurrentUser(imageSearchDto: ImageSearchDto): List<ImageListDto> {
        val id = imageServiceUtils.getCurrentUserId()
        val employee = employeeDataProvider.findById(id)
        val startDate = imageServiceUtils.localDateParse(imageSearchDto.startDate)
        val endDate = imageServiceUtils.localDateParse(imageSearchDto.endDate)
        val images = imageDataProvider.findAllByEmployeeIdAndDateRange(id, startDate, endDate)
        return images.map { image -> imageServiceUtils.convertToImageListDto(image, employee) }
    }

    @Transactional
    override fun storeImageForCurrentUser(uploadImageRequestDto: UploadImageRequestDto): Image {
        try {
            val userId = imageServiceUtils.getCurrentUserId()
            val employee = employeeDataProvider.findById(userId)
            val filePath = imageServiceUtils.storeFile(uploadImageRequestDto.file)
            val image = Image(
                filePath = filePath,
                message = uploadImageRequestDto.message,
                employee = employee,
                uploadDate = LocalDate.now()
            )
            return imageDataProvider.save(image)
        } catch (ex: Exception) {
            throw DataIntegrityViolationException(ex.message!!)

        }
    }

    @Transactional
    override fun deleteImageByIdForCurrentUser(imageId: Long) {
        try {

            val userId = imageServiceUtils.getCurrentUserId()
            val image = imageDataProvider.findByIdAndEmployeeId(imageId, userId)
                ?: throw ObjectNotFoundException("$NO_IMAGE_FOUND_WITH_ID_: $imageId")
            imageDataProvider.delete(image)
            imageServiceUtils.deleteFile(image.filePath!!)
        } catch (ex: Exception) {
            throw DataIntegrityViolationException(ex.message!!)
        }
    }

    @Transactional
    override fun updateImageMessageForCurrentUser(
        imageId: Long,
        updateImageMessageDto: UpdateImageMessageDto
    ): ImageListDto {
        try {

            val userId = imageServiceUtils.getCurrentUserId()
            val image = imageDataProvider.findByIdAndEmployeeId(imageId, userId)
                ?: throw ObjectNotFoundException("$NO_IMAGE_FOUND_WITH_ID_: $imageId")
            image.message = updateImageMessageDto.message
            val updatedImage = imageDataProvider.save(image)
            return imageServiceUtils.convertToImageListDto(updatedImage, image.employee!!)
        } catch (ex: Exception) {
            throw DataIntegrityViolationException(ex.message)

        }
    }


    override fun getImagesByEmployeeCpfAsManager(
        managerImageRequestDto: ManagerImageRequestDto,
        imageSearchDto: ImageSearchDto
    ): List<ImageListDto> {
        try {
            val employee = imageServiceUtils.validateSameCompany(managerImageRequestDto)
            val startDate = imageServiceUtils.localDateParse(imageSearchDto.startDate)
            val endDate = imageServiceUtils.localDateParse(imageSearchDto.endDate)
            val images = imageDataProvider.findAllByEmployeeCpfAndDateRange(
                managerImageRequestDto.employeeCpf,
                startDate,
                endDate
            )
            return images.map { image -> imageServiceUtils.convertToImageListDto(image, employee) }
        } catch (ex: Exception) {
            throw DataIntegrityViolationException(ex.message)

        }
    }

    @Transactional

    override fun deleteImageByIdAsManager(managerImageRequestDto: ManagerImageRequestDto) {
        try {
            imageServiceUtils.validateSameCompany(managerImageRequestDto)
            val image =
                imageDataProvider.findByIdAndEmployeeCpf(
                    managerImageRequestDto.imageId,
                    managerImageRequestDto.employeeCpf
                )
                    ?: throw ObjectNotFoundException("$NO_IMAGE_FOUND_WITH_ID_: ${managerImageRequestDto.imageId}")
            imageDataProvider.delete(image)
            imageServiceUtils.deleteFile(image.filePath!!)
        } catch (ex: Exception) {
            throw DataIntegrityViolationException(ex.message)

        }
    }

    @Transactional
    override fun updateImageMessageAsManager(managerImageRequestDto: ManagerImageRequestDto): ImageListDto {
        try {
            imageServiceUtils.validateSameCompany(managerImageRequestDto)
            val image = imageDataProvider.findByIdAndEmployeeCpf(
                managerImageRequestDto.imageId,
                managerImageRequestDto.employeeCpf
            )
                ?: throw ObjectNotFoundException("$NO_IMAGE_FOUND_WITH_ID_: ${managerImageRequestDto.imageId}")
            image.message = managerImageRequestDto.updateImageMessageDto?.message
            val updatedImage = imageDataProvider.save(image)
            return imageServiceUtils.convertToImageListDto(updatedImage, image.employee!!)
        } catch (ex: Exception) {
            throw DataIntegrityViolationException(ex.message)

        }
    }

    @Transactional

    override fun getImageByIdAsManager(managerImageRequestDto: ManagerImageRequestDto): ByteArray {
        try {
            imageServiceUtils.validateSameCompany(managerImageRequestDto)
            val image = imageDataProvider.findByIdAndEmployeeCpf(
                managerImageRequestDto.imageId,
                managerImageRequestDto.employeeCpf
            )
                ?: throw ObjectNotFoundException("$NO_IMAGE_FOUND_WITH_ID_: ${managerImageRequestDto.imageId}")

            return imageServiceUtils.loadFileAsResource(image.filePath!!)
        } catch (ex: Exception) {
            throw DataIntegrityViolationException(ex.message)

        }
    }


}
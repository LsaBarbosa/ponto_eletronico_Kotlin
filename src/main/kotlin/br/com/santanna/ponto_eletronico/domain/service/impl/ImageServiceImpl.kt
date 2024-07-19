package br.com.santanna.ponto_eletronico.domain.service.impl

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
import java.time.format.DateTimeFormatter

private const val NO_IMAGE_FOUND_WITH_ID_ = "Nenhuma imagem para o ID: "


private const val FOR_EMPLOYEE_WITH_CPF_ = "registrada no CPF: "

@Service
class ImageServiceImpl(

    private val imageDataProvider: ImageDataProvider,
    private val employeeDataProvider: EmployeeDataProvider,
    private val imageServiceUtils: ImageServiceUtils,

) : ImageService {

    override fun getImageByIdForCurrentUser(imageId: Long): ByteArray {
        val userId = imageServiceUtils.getCurrentUserId()
        val image = imageDataProvider.findByIdAndEmployeeId(imageId, userId)
            ?: throw ObjectNotFoundException("No image found for user ID: $userId and image ID: $imageId")
        return imageServiceUtils.loadFileAsResource(image.filePath!!)
    }
    override fun getImagesForCurrentUser(imageSearchDto: ImageSearchDto): List<ImageListDto> {
        val id = imageServiceUtils.getCurrentUserId()
        val employee = employeeDataProvider.findById(id)
        val startDate = LocalDate.parse(imageSearchDto.startDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        val endDate = LocalDate.parse(imageSearchDto.endDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        val images = imageDataProvider.findAllByEmployeeIdAndDateRange(id, startDate, endDate)
        return images.map { image -> imageServiceUtils.convertToImageListDto(image, employee) }
    }
    @Transactional
    override fun storeImage(uploadImageRequestDto: UploadImageRequestDto): Image {
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
    }
    @Transactional
    override fun deleteImageByIdForCurrentUser(imageId: Long) {
        val userId = imageServiceUtils.getCurrentUserId()
        val image = imageDataProvider.findByIdAndEmployeeId(imageId, userId)
            ?: throw ObjectNotFoundException("No image found for ID: $imageId and Employee ID: $userId")
        imageDataProvider.delete(image)
        imageServiceUtils.deleteFile(image.filePath!!)

    }
    @Transactional
    override fun updateImageMessageForCurrentUser(imageId: Long,updateImageMessageDto: UpdateImageMessageDto): ImageListDto {
        val userId = imageServiceUtils.getCurrentUserId()
        val image = imageDataProvider.findByIdAndEmployeeId(imageId, userId)
            ?: throw ObjectNotFoundException("No image found with ID: $imageId for employee with ID: $userId")
        image.message = updateImageMessageDto.message
        val updatedImage = imageDataProvider.save(image)
        return imageServiceUtils.convertToImageListDto(updatedImage, image.employee!!)
    }


    override fun getImagesByEmployeeCpfAsManager(managerImageRequestDto: ManagerImageRequestDto, imageSearchDto: ImageSearchDto): List<ImageListDto> {

        val employee = imageServiceUtils.validateSameCompany(managerImageRequestDto)
        val startDate = LocalDate.parse(imageSearchDto.startDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        val endDate = LocalDate.parse(imageSearchDto.endDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        val images = imageDataProvider.findAllByEmployeeCpfAndDateRange(managerImageRequestDto.employeeCpf, startDate, endDate)
        return images.map { image -> imageServiceUtils.convertToImageListDto(image, employee) }
    }

    @Transactional
    override fun deleteImageByIdAsManager(managerImageRequestDto: ManagerImageRequestDto) {
        imageServiceUtils.validateSameCompany(managerImageRequestDto)
        val image = imageDataProvider.findByIdAndEmployeeCpf(managerImageRequestDto.imageId, managerImageRequestDto.employeeCpf)
            ?: throw ObjectNotFoundException("No image found with ID: ${managerImageRequestDto.imageId} for employee with CPF: ${managerImageRequestDto.employeeCpf}")
        imageDataProvider.delete(image)
        imageServiceUtils.deleteFile(image.filePath!!)
    }

    @Transactional
    override fun updateImageMessageAsManager(managerImageRequestDto: ManagerImageRequestDto): ImageListDto {
        imageServiceUtils.validateSameCompany(managerImageRequestDto)
        val image = imageDataProvider.findByIdAndEmployeeCpf(managerImageRequestDto.imageId, managerImageRequestDto.employeeCpf)
            ?: throw ObjectNotFoundException("No image found with ID: ${managerImageRequestDto.imageId} for employee with CPF: ${managerImageRequestDto.employeeCpf}")
        image.message = managerImageRequestDto.updateImageMessageDto?.message
        val updatedImage = imageDataProvider.save(image)
        return imageServiceUtils.convertToImageListDto(updatedImage, image.employee!!)
    }

    @Transactional
    override fun getImageByIdAsManager(managerImageRequestDto: ManagerImageRequestDto): ByteArray {
        imageServiceUtils.validateSameCompany(managerImageRequestDto)
        val image = imageDataProvider.findByIdAndEmployeeCpf(managerImageRequestDto.imageId, managerImageRequestDto.employeeCpf)
            ?: throw ObjectNotFoundException("No image found with ID: ${managerImageRequestDto.imageId} for employee with CPF: ${managerImageRequestDto.employeeCpf}")

        return imageServiceUtils.loadFileAsResource(image.filePath!!)
    }


}
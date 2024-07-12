package br.com.santanna.ponto_eletronico.domain.service.impl

import br.com.santanna.ponto_eletronico.app.handler.model.ObjectNotFoundException
import br.com.santanna.ponto_eletronico.domain.dataprovider.EmployeeDataProvider
import br.com.santanna.ponto_eletronico.domain.dto.image.*
import br.com.santanna.ponto_eletronico.domain.entity.Image
import br.com.santanna.ponto_eletronico.domain.service.ImageService
import br.com.santanna.ponto_eletronico.infrastructure.repository.ImageRepository
import jakarta.transaction.Transactional
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

private const val NO_IMAGE_FOUND_WITH_ID_ = "Nenhuma imagem para o ID: "


private const val FOR_EMPLOYEE_WITH_CPF_ = "registrada no CPF: "

@Service
class ImageServiceImpl(
    private val imageRepository: ImageRepository,
    private val employeeDataProvider: EmployeeDataProvider,

) : ImageService {

    @Transactional
    override fun storeImage(uploadImageDto: UploadImageRequestDto): Image {
        val employee = employeeDataProvider.findCpf(uploadImageDto.cpf)
            ?: throw ObjectNotFoundException("Employee not found with CPF: ${uploadImageDto.cpf}")

        val encryptedPassword = BCryptPasswordEncoder().matches(uploadImageDto.passwords, employee.password)

        if (!encryptedPassword) {
            throw IllegalArgumentException("Invalid password")
        }

        val filePath = storeFile(uploadImageDto.file)
        val image = Image(
            filePath = filePath,
            message = uploadImageDto.message,
            employee = employee,
            uploadDate = LocalDate.now()
        )

        return imageRepository.save(image)
    }

    override fun getImageByEmployeeCpf(cpf: String): ByteArray {
        val image = imageRepository.findByEmployeeCpf(cpf)
            ?: throw ObjectNotFoundException("No image found $FOR_EMPLOYEE_WITH_CPF_ $cpf")
        return loadFileAsResource(image.filePath!!)
    }

    override fun getImagesByEmployeeCpf(cpf: String): List<ImageListDto> {
        val employee = employeeDataProvider.findCpf(cpf)
            ?: throw ObjectNotFoundException("Employee not found with CPF: $cpf")
        val images = imageRepository.findAllByEmployeeCpf(cpf)
        return images.map { image ->
            ImageListDto(
                id = image.id,
                name = employee.name,
                surname = employee.surname,
                cpf = employee.cpf,
                filePath = image.filePath,
                message = image.message,
                uploadDate = image.uploadDate
            )
        }
    }

    override fun getImageById(id: Long): ByteArray {
        val image = imageRepository.findById(id)
            .orElseThrow { ObjectNotFoundException("$NO_IMAGE_FOUND_WITH_ID_ $id") }
        return loadFileAsResource(image.filePath!!)
    }


    @Transactional
    override fun deleteImageById(deleteImageRequestDto: DeleteImageRequestDto){
        val employee = employeeDataProvider.findCpf(deleteImageRequestDto.employeeCpfTarget)
            ?: throw ObjectNotFoundException("Employee not found with CPF: ${deleteImageRequestDto.employeeCpfTarget}")

        val deletingEmployee = employeeDataProvider.findCpf(deleteImageRequestDto.employeeManagerCpf)
            ?: throw ObjectNotFoundException("Employee not found with CPF: ${deleteImageRequestDto.employeeManagerCpf}")

        val isPasswordValid = BCryptPasswordEncoder().matches(deleteImageRequestDto.passwords, deletingEmployee.password)
        if (!isPasswordValid) {
            throw IllegalArgumentException("Invalid password")
        }

        val image = imageRepository.findByIdAndEmployeeCpf(deleteImageRequestDto.imageId, deleteImageRequestDto.employeeCpfTarget)
            ?: throw ObjectNotFoundException("$NO_IMAGE_FOUND_WITH_ID_ ${deleteImageRequestDto.imageId} $FOR_EMPLOYEE_WITH_CPF_ ${deleteImageRequestDto.employeeCpfTarget}")

        imageRepository.delete(image)
        deleteFile(image.filePath!!)
    }
    @Transactional
    override fun updateImageMessage(updateImageRequestDto: UpdateImageRequestDto): ImageListDto {
        val employeeTarget = employeeDataProvider.findCpf(updateImageRequestDto.employeeCpfTarget)
            ?: throw ObjectNotFoundException("Employee not found with CPF: ${updateImageRequestDto.employeeCpfTarget}")

        val updatingEmployee = employeeDataProvider.findCpf(updateImageRequestDto.employeeManagerCpf)
            ?: throw ObjectNotFoundException("Employee not found with CPF: ${updateImageRequestDto.employeeManagerCpf}")

        val isPasswordValid = BCryptPasswordEncoder().matches(updateImageRequestDto.passwords, updatingEmployee.password)
        if (!isPasswordValid) {
            throw IllegalArgumentException("Invalid password")
        }

        val image = imageRepository.findByIdAndEmployeeCpf(updateImageRequestDto.imageId, updateImageRequestDto.employeeCpfTarget)
            ?: throw ObjectNotFoundException("$NO_IMAGE_FOUND_WITH_ID_ ${updateImageRequestDto.imageId} $FOR_EMPLOYEE_WITH_CPF_ ${updateImageRequestDto.employeeCpfTarget}")

        image.message = updateImageRequestDto.updateImageMessageDto.message
        val updatedImage = imageRepository.save(image)
        val employee = updatedImage.employee!!

        return ImageListDto(
            id = updatedImage.id,
            name = employee.name,
            surname = employee.surname,
            cpf = employee.cpf,
            filePath = updatedImage.filePath,
            message = updatedImage.message,
            uploadDate = updatedImage.uploadDate
        )
    }

    override fun getImagesByEmployeeCpfAndDateRange(imageSearchDto: ImageSearchDto): List<ImageListDto> {
        val employee = employeeDataProvider.findCpf(imageSearchDto.cpf)
            ?: throw ObjectNotFoundException("Employee not found with CPF: ${imageSearchDto.cpf}")

        val isPasswordValid = BCryptPasswordEncoder().matches(imageSearchDto.passwords, employee.password)
        if (!isPasswordValid) {
            throw IllegalArgumentException("Invalid password")
        }

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val startDate = LocalDate.parse(imageSearchDto.startDate, formatter)
        val endDate = LocalDate.parse(imageSearchDto.endDate, formatter)

        val images = imageRepository.findAllByEmployeeCpfAndDateRange(imageSearchDto.cpf, startDate, endDate)
        return images.map { image ->
            ImageListDto(
                id = image.id,
                name = employee.name,
                surname = employee.surname,
                cpf = employee.cpf,
                filePath = image.filePath,
                message = image.message,
                uploadDate = image.uploadDate
            )
        }
    }

    private fun deleteFile(filePath: String) {
        try {
            val file = Paths.get(filePath).toAbsolutePath().normalize()
            Files.deleteIfExists(file)
        } catch (ex: IOException) {
            throw RuntimeException("Could not delete file: $filePath", ex)
        }
    }

    private val uploadDirectory: Path = Paths.get("uploads")

    init {
        Files.createDirectories(uploadDirectory)
    }

    private fun storeFile(file: MultipartFile): String {
        val fileName = UUID.randomUUID().toString() + "-" + file.originalFilename
        val targetLocation = uploadDirectory.resolve(fileName)
        Files.copy(file.inputStream, targetLocation)
        return targetLocation.toString()
    }

    @Throws(IOException::class)
    private fun loadFileAsResource(filePath: String): ByteArray {
        val path = Paths.get(filePath)
        return Files.readAllBytes(path)
    }


}
package br.com.santanna.ponto_eletronico.domain.service.impl

import br.com.santanna.ponto_eletronico.app.handler.model.ObjectNotFoundException
import br.com.santanna.ponto_eletronico.domain.dataprovider.EmployeeDataProvider
import br.com.santanna.ponto_eletronico.domain.dto.image.ImageListDto
import br.com.santanna.ponto_eletronico.domain.dto.image.UpdateImageMessageDto
import br.com.santanna.ponto_eletronico.domain.dto.image.UploadImageRequestDto
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
import java.util.*

private const val NO_IMAGE_FOUND_WITH_ID_ = "Nenhuma imagem para o ID: "


private const val FOR_EMPLOYEE_WITH_CPF_ = "registrada no CPF: "

@Service
class ImageServiceImpl(
    private val imageRepository: ImageRepository,
    private val employeeDataProvider: EmployeeDataProvider,

) : ImageService {

    @Transactional
    override fun storeImage(uploadImageRequestDto: UploadImageRequestDto): Image {
        val employee = employeeDataProvider.findCpf(uploadImageRequestDto.cpf)
            ?: throw ObjectNotFoundException("Employee not found with CPF: $uploadImageRequestDto.cpf")

        val encryptedPassword = BCryptPasswordEncoder().matches(uploadImageRequestDto.passwords,employee.password)

        if (!encryptedPassword) {
            throw IllegalArgumentException("Invalid password")
        }
        val filePath = storeFile(uploadImageRequestDto.file)
        val image = Image(filePath = filePath, message = uploadImageRequestDto.message, employee = employee)
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
                message = image.message
            )
        }
    }

    override fun getImageById(id: Long): ByteArray {
        val image = imageRepository.findById(id)
            .orElseThrow { ObjectNotFoundException("$NO_IMAGE_FOUND_WITH_ID_ $id") }
        return loadFileAsResource(image.filePath!!)
    }


    @Transactional
    override fun deleteImageById(id: Long, cpf: String) {
        val image = imageRepository.findByIdAndEmployeeCpf(id, cpf)
            ?: throw ObjectNotFoundException("$NO_IMAGE_FOUND_WITH_ID_ $id $FOR_EMPLOYEE_WITH_CPF_ $cpf")
        imageRepository.delete(image)
        deleteFile(image.filePath!!)
    }

    @Transactional
    override fun updateImageMessage(id: Long, cpf: String, updateImageMessageDto: UpdateImageMessageDto): ImageListDto {
        val image = imageRepository.findByIdAndEmployeeCpf(id, cpf)
            ?: throw ObjectNotFoundException("$NO_IMAGE_FOUND_WITH_ID_ $id $FOR_EMPLOYEE_WITH_CPF_ $cpf")

        val employeePassword = image.employee
        val encryptedPassword = BCryptPasswordEncoder().matches(updateImageMessageDto.passwords,employeePassword?.password)

        if (!encryptedPassword) {
            throw IllegalArgumentException("Invalid password")
        }
        image.message = updateImageMessageDto.message
        val updatedImage = imageRepository.save(image)
        val employee = updatedImage.employee!!
        return ImageListDto(
            id = updatedImage.id,
            name = employee.name,
            surname = employee.surname,
            cpf = employee.cpf,
            filePath = updatedImage.filePath,
            message = updatedImage.message
        )
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
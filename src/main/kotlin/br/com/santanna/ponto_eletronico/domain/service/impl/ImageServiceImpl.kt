package br.com.santanna.ponto_eletronico.domain.service.impl

import br.com.santanna.ponto_eletronico.app.handler.model.ObjectNotFoundException
import br.com.santanna.ponto_eletronico.domain.dataprovider.EmployeeDataProvider
import br.com.santanna.ponto_eletronico.domain.dto.image.ImageListDto
import br.com.santanna.ponto_eletronico.domain.entity.Image
import br.com.santanna.ponto_eletronico.domain.service.ImageService
import br.com.santanna.ponto_eletronico.infrastructure.repository.ImageRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

@Service
class ImageServiceImpl(
    private val imageRepository: ImageRepository,
    private val employeeDataProvider: EmployeeDataProvider,
) : ImageService {

    @Transactional
    override fun storeImage(cpf: String, file: MultipartFile, message: String?): Image {
        val employee = employeeDataProvider.findCpf(cpf)
            ?: throw ObjectNotFoundException("Employee not found with CPF: $cpf")

        val filePath = storeFile(file)
        val image = Image(filePath = filePath, message = message, employee = employee)
        return imageRepository.save(image)
    }

    override fun getImageByEmployeeCpf(cpf: String): ByteArray {
        val image = imageRepository.findByEmployeeCpf(cpf)
            ?: throw ObjectNotFoundException("No image found for employee with CPF: $cpf")
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
            .orElseThrow { ObjectNotFoundException("No image found with ID: $id") }
        return loadFileAsResource(image.filePath!!)
    }


    @Transactional
    override fun deleteImageById(id: Long) {
        val image = imageRepository.findById(id)
            .orElseThrow { ObjectNotFoundException("No image found with ID: $id") }
        imageRepository.delete(image)
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
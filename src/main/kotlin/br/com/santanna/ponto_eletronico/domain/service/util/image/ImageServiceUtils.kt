package br.com.santanna.ponto_eletronico.domain.service.util.image

import br.com.santanna.ponto_eletronico.domain.dataprovider.EmployeeDataProvider
import br.com.santanna.ponto_eletronico.domain.dto.image.search.ImageListDto
import br.com.santanna.ponto_eletronico.domain.entity.Employee
import br.com.santanna.ponto_eletronico.domain.entity.Image
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

@Component
class ImageServiceUtils (private val employeeDataProvider: EmployeeDataProvider) {


    fun deleteFile(filePath: String) {
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

    fun storeFile(file: MultipartFile): String {
        val fileName = UUID.randomUUID().toString() + "-" + file.originalFilename
        val targetLocation = uploadDirectory.resolve(fileName)
        Files.copy(file.inputStream, targetLocation)
        return targetLocation.toString()
    }

    @Throws(IOException::class)
    fun loadFileAsResource(filePath: String): ByteArray {
        val path = Paths.get(filePath)
        return Files.readAllBytes(path)
    }

    fun convertToImageListDto(image: Image, employee: Employee): ImageListDto {
        return ImageListDto(
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
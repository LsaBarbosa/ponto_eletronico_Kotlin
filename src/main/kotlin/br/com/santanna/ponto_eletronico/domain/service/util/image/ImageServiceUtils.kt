package br.com.santanna.ponto_eletronico.domain.service.util.image

import br.com.santanna.ponto_eletronico.domain.dataprovider.EmployeeDataProvider
import br.com.santanna.ponto_eletronico.domain.dto.image.search.ImageListDto
import br.com.santanna.ponto_eletronico.domain.entity.Image
import br.com.santanna.ponto_eletronico.domain.entity.company.Company
import br.com.santanna.ponto_eletronico.domain.entity.employee.Employee
import br.com.santanna.ponto_eletronico.domain.entity.employee.EmployeeRole
import br.com.santanna.ponto_eletronico.infrastructure.security.jwt.JwtTokenUtil
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class ImageServiceUtils (private val employeeDataProvider: EmployeeDataProvider, private val jwtTokenUtil: JwtTokenUtil) {


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
            uploadDate = image.uploadDate?.format(dateFormatter)
        )
    }

    fun validateSameCompanyById(employeeId: UUID): Employee {
        val company = validateManagerRole()
        val employee = employeeDataProvider.findById(employeeId)

        if (employee.company?.id != company.id) {
            throw IllegalArgumentException("The specified employee does not belong to the manager's company.")
        }
        return employee
    }

    fun validateManagerRole(): Company {
        val id = getCurrentUserId()
        val manager = employeeDataProvider.findById(id)

        if (manager.role != EmployeeRole.MANAGER) {
            throw IllegalArgumentException("The specified employee is not a manager.")
        }

        val company = manager.company
            ?: throw IllegalArgumentException("Manager does not belong to any company.")

        return company
    }

     fun getCurrentUserId(): UUID {
        val authentication = SecurityContextHolder.getContext().authentication as UsernamePasswordAuthenticationToken
        val token = authentication.credentials as String
        return jwtTokenUtil.getUserIdFromToken(token)
    }

    fun localDateParse(date:String): LocalDate =
        LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
}
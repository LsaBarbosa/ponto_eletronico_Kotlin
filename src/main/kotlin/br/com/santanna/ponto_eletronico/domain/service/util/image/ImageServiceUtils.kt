package br.com.santanna.ponto_eletronico.domain.service.util.image

import br.com.santanna.ponto_eletronico.app.handler.model.ObjectNotFoundException
import br.com.santanna.ponto_eletronico.domain.dataprovider.EmployeeDataProvider
import br.com.santanna.ponto_eletronico.domain.dto.image.manager.ManagerImageRequestDto
import br.com.santanna.ponto_eletronico.domain.dto.image.search.ImageListDto
import br.com.santanna.ponto_eletronico.domain.entity.Company
import br.com.santanna.ponto_eletronico.domain.entity.Employee
import br.com.santanna.ponto_eletronico.domain.entity.EmployeeRole
import br.com.santanna.ponto_eletronico.domain.entity.Image
import br.com.santanna.ponto_eletronico.infrastructure.security.JwtTokenUtil
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

@Component
class ImageServiceUtils (private val employeeDataProvider: EmployeeDataProvider, private val jwtTokenUtil: JwtTokenUtil,) {


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

     fun validateSameCompany(managerImageRequestDto: ManagerImageRequestDto): Employee {
        val company = validateManagerRole()
        val employee = validateEmployee(managerImageRequestDto.employeeCpf)

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

     fun validateEmployee(cpf: String): Employee {
        return employeeDataProvider.findCpf(cpf)
            ?: throw ObjectNotFoundException("Employee not found with CPF: $cpf")
    }

     fun getCurrentUserId(): UUID {
        val authentication = SecurityContextHolder.getContext().authentication as UsernamePasswordAuthenticationToken
        val token = authentication.credentials as String
        return jwtTokenUtil.getUserIdFromToken(token)
    }
}
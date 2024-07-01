package br.com.santanna.ponto_eletronico.app.entrypoint.http

import br.com.santanna.ponto_eletronico.domain.dto.employee.EmployeeDto
import br.com.santanna.ponto_eletronico.domain.dto.employee.EmployeeGetDto
import br.com.santanna.ponto_eletronico.domain.service.EmployeeService
import br.com.santanna.ponto_eletronico.infrastructure.security.token.Auth
import br.com.santanna.ponto_eletronico.infrastructure.security.token.AuthenticationDTO
import br.com.santanna.ponto_eletronico.infrastructure.security.token.LoginResponseDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/colaborador")
class EmployeeController(val employeeService: EmployeeService, val authService: Auth) {

    @GetMapping
    fun allEmployees(): List<EmployeeGetDto> {
        return employeeService.getAllEmployees()
    }

    @GetMapping("/{id}")
    fun getEmployeeById(@PathVariable("id") id: Long): ResponseEntity<EmployeeGetDto?> {
        val employee = employeeService.getEmployeeById(id)
        return ResponseEntity.ok(employee)

    }

    @GetMapping("/busca") // Removemos o /{name} do path
    fun getEmployeeByNameAndSurname(
        @RequestParam("name") name: String,
        @RequestParam("surname") surname: String
    ): ResponseEntity<EmployeeGetDto?> {

        val employee = employeeService.getEmployeeByNameAndSurname(name, surname)
        return ResponseEntity.ok(employee)

    }

    @PostMapping("criar-colaborador")
    fun registerNewEmployee(@RequestBody employeeDto: EmployeeDto): ResponseEntity<EmployeeDto> {
        val employeeCreated = employeeService.registerEmployee(employeeDto)
        val uri = URI.create("/employees/${employeeCreated.id}")
        return ResponseEntity.created(uri).body(employeeCreated)

    }

    @PutMapping
    fun updateEmployee(@RequestBody employeeDto: EmployeeDto): ResponseEntity<EmployeeDto> {
        if (employeeDto.name == null || employeeDto.surname == null) {
            throw IllegalArgumentException("Name and surname are required for update.")
        }

        val updatedEmployeeDto = employeeService.updateEmployee(employeeDto)
        return ResponseEntity.ok(updatedEmployeeDto)

    }
    @PostMapping("/login")
    fun login(@RequestBody data: AuthenticationDTO): ResponseEntity<LoginResponseDTO> {
        val token = authService.login(data)
        return ResponseEntity.ok(token)
    }

    @DeleteMapping
    fun deleteEmployee(@RequestParam name: String, @RequestParam surname: String): ResponseEntity<Void> {
        employeeService.deleteEmployee(name, surname)
        return ResponseEntity.noContent().build()

    }


}
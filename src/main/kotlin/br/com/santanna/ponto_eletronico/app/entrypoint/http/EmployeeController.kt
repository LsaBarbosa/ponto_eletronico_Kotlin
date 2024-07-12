package br.com.santanna.ponto_eletronico.app.entrypoint.http

import br.com.santanna.ponto_eletronico.domain.dto.employee.*
import br.com.santanna.ponto_eletronico.domain.service.EmployeeService
import br.com.santanna.ponto_eletronico.infrastructure.security.login.Auth
import br.com.santanna.ponto_eletronico.infrastructure.security.login.AuthenticationDTO
import br.com.santanna.ponto_eletronico.infrastructure.security.login.LoginResponseDTO
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/colaborador")
class EmployeeController(val employeeService: EmployeeService, val authService: Auth) {

    @GetMapping
    fun allEmployees(pageable: Pageable): ResponseEntity<Page<EmployeeGetDto>> {
        val employees = employeeService.getAllEmployees(pageable)
        return ResponseEntity.ok(employees)
    }

    @GetMapping("/{id}")
    fun getEmployeeById(@PathVariable("id") id: Long): ResponseEntity<EmployeeGetDto?> {
        val employee = employeeService.getEmployeeById(id)
        return ResponseEntity.ok(employee)

    }

    @GetMapping("/busca-nome")
    fun getEmployeeByNameAndSurname(
        @RequestParam("name") name: String,
        @RequestParam("surname") surname: String
    ): ResponseEntity<EmployeeGetDto?> {

        val employee = employeeService.getEmployeeByNameAndSurname(name, surname)
        return ResponseEntity.ok(employee)

    }
    @GetMapping("/busca-cpf")
    fun getEmployeeByCpf(@RequestParam("cpf") cpf: String ): ResponseEntity<EmployeeGetDto?> {
        val employee = employeeService.getEmployeeByCpf(cpf)
        return ResponseEntity.ok(employee)

    }

    @PostMapping("criar-colaborador")
    fun registerNewEmployee(@Valid @RequestBody employeeDto: EmployeeDto): ResponseEntity<EmployeeDto> {
        val employeeCreated = employeeService.registerEmployee(employeeDto)
        val uri = URI.create("/employees/${employeeCreated.id}")
        return ResponseEntity.created(uri).body(employeeCreated)

    }

    @PutMapping
    fun updateEmployee(@Valid @RequestBody updateEmployeeRequestDto: UpdateEmployeeRequestDto): ResponseEntity<UpdateEmployeeDto> {
        val updatedEmployeeDto = employeeService.updateEmployee(updateEmployeeRequestDto)
        return ResponseEntity.ok(updatedEmployeeDto)
    }

    @PostMapping("/login")
    fun login(@RequestBody data: AuthenticationDTO): ResponseEntity<LoginResponseDTO> {
        val token = authService.login(data)
        return ResponseEntity.ok(token)
    }

    @DeleteMapping
    fun deleteEmployee(@RequestBody deleteEmployeeRequestDto: DeleteEmployeeRequestDto): ResponseEntity<Void> {
        employeeService.deleteEmployee(deleteEmployeeRequestDto)
        return ResponseEntity.noContent().build()
    }


}
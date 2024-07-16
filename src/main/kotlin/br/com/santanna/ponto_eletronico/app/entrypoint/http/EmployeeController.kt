package br.com.santanna.ponto_eletronico.app.entrypoint.http

import br.com.santanna.ponto_eletronico.domain.dto.employee.*
import br.com.santanna.ponto_eletronico.domain.service.EmployeeService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.util.*

@RestController
@RequestMapping("/colaborador")
class EmployeeController(val employeeService: EmployeeService ) {


    @GetMapping("/{id}")
    fun getEmployeeById(@PathVariable("id") id: UUID): ResponseEntity<EmployeeGetDto?> {
        val employee = employeeService.getEmployeeById(id)
        return ResponseEntity.ok(employee)

    }

    @GetMapping("/meus-colaboradores")
    fun getEmployeesByManager(
        @Valid @RequestBody managerEmployeeRequestDto: ManagerEmployeeRequestDto,
        pageable: Pageable
    ): ResponseEntity<Page<EmployeeGetDto>> {
        val employees = employeeService.getEmployeesByManager(managerEmployeeRequestDto, pageable)
        return ResponseEntity.ok(employees)
    }

    @PutMapping("/update-password")
    fun updatePassword(@Valid @RequestBody updatePasswordDto: UpdatePassword): ResponseEntity<Void> {
        employeeService.updatePassword(updatePasswordDto)
        return ResponseEntity.ok().build()
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
    fun getEmployeeByCpf(@Valid @RequestBody request: ManagerEmployeeRequestByCPFDto): ResponseEntity<EmployeeGetDto?> {
        val employee = employeeService.getEmployeeByCpf(request)
        return ResponseEntity.ok(employee)
    }

    @PostMapping("criar-colaborador")
    fun registerNewEmployee(
        @RequestParam("managerCpf") managerCpf: String,
        @Valid @RequestBody createEmployeeDto: CreateEmployeeDto
    ): ResponseEntity<EmployeeDto> {
        val employeeCreated = employeeService.registerEmployee(managerCpf, createEmployeeDto)
        val uri = URI.create("/employees/${employeeCreated.id}")
        return ResponseEntity.created(uri).body(employeeCreated)
    }

    @PutMapping
    fun updateEmployee(@Valid @RequestBody updateEmployeeRequestDto: UpdateEmployeeRequestDto): ResponseEntity<UpdateEmployeeDto> {
        val updatedEmployeeDto = employeeService.updateEmployee(updateEmployeeRequestDto)
        return ResponseEntity.ok(updatedEmployeeDto)
    }



    @DeleteMapping
    fun deleteEmployee(@RequestBody deleteEmployeeRequestDto: DeleteEmployeeRequestDto): ResponseEntity<Void> {
        employeeService.deleteEmployee(deleteEmployeeRequestDto)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/reset-password")
    fun resetPassword(@Valid @RequestBody resetPasswordDto: ResetPasswordDto): ResponseEntity<Void> {
        employeeService.resetPassword(resetPasswordDto)
        return ResponseEntity.ok().build()
    }

}
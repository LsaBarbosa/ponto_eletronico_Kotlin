package br.com.santanna.ponto_eletronico.app.entrypoint.http

import br.com.santanna.ponto_eletronico.domain.dto.employee.*
import br.com.santanna.ponto_eletronico.domain.service.EmployeeService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/colaborador")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Employee", description = "End-point para gestão de funcionários")
class EmployeeController(val employeeService: EmployeeService ) {

    @GetMapping("/id")
    @Operation(summary = "Buscar funcionário pelo id")

    fun getEmployeeById(): ResponseEntity<EmployeeGetDto> {
        return ResponseEntity.ok(employeeService.getEmployeeById())
    }

    @GetMapping("/meus-colaboradores")
    @Operation(summary = "Administrador busca todos os funcionários")
    fun getEmployeesByManager(@Valid @RequestBody managerEmployeeRequestDto: ManagerEmployeeRequestDto, pageable: Pageable): ResponseEntity<Page<EmployeeGetDto>> {
        val employees = employeeService.getEmployeesByManager(managerEmployeeRequestDto, pageable)
        return ResponseEntity.ok(employees)
    }

    @GetMapping("/busca-nome")
    @Operation(summary = "Administrador busca funcionário pelo nome")
    fun getEmployeeByNameAndSurname(@RequestParam("name") name: String, @RequestParam("surname") surname: String
    ): ResponseEntity<EmployeeGetDto?> {

        val employee = employeeService.getEmployeeByNameAndSurname(name, surname)
        return ResponseEntity.ok(employee)

    }

    @GetMapping("/busca-cpf")
    @Operation(summary = "Administrador busca funcionário pelo cpf")
    fun getEmployeeByCpfForManage(@Valid @RequestBody request: ManagerEmployeeRequestByCPFDto): ResponseEntity<EmployeeGetDto?> {
        val employee = employeeService.getEmployeeByCpf(request)
        return ResponseEntity.ok(employee)
    }

    @PostMapping("criar-colaborador")
    @Operation(summary = "Administrador registra um funcionário")
    fun registerNewEmployee(@Valid @RequestBody createEmployeeDto: CreateEmployeeDto
    ): ResponseEntity<EmployeeDto> {
        val employeeCreated = employeeService.registerEmployee( createEmployeeDto)
        val uri = URI.create("/employees/${employeeCreated.id}")
        return ResponseEntity.created(uri).body(employeeCreated)
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reseta a senha do funcionário")
    fun resetPassword(@Valid @RequestBody resetPasswordDto: ResetPasswordDto): ResponseEntity<Void> {
        employeeService.resetPassword(resetPasswordDto)
        return ResponseEntity.ok().build()
    }

    @PutMapping
    @Operation(summary = "Administrador atualiza dados do funcionário")
    fun updateEmployee(@Valid @RequestBody updateEmployeeRequestDto: UpdateEmployeeRequestDto): ResponseEntity<UpdateEmployeeDto> {
        val updatedEmployeeDto = employeeService.updateEmployee(updateEmployeeRequestDto)
        return ResponseEntity.ok(updatedEmployeeDto)
    }

    @PutMapping("/update-password")
    @Operation(summary = "Alteração de senha")
    fun updatePassword(@Valid @RequestBody updatePasswordDto: UpdatePassword): ResponseEntity<Void> {
        employeeService.updatePassword(updatePasswordDto)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping
    @Operation(summary = "Administrador apaga funcionário")
    fun deleteEmployee(@RequestBody deleteEmployeeRequestDto: DeleteEmployeeRequestDto): ResponseEntity<Void> {
        employeeService.deleteEmployee(deleteEmployeeRequestDto)
        return ResponseEntity.noContent().build()
    }


}
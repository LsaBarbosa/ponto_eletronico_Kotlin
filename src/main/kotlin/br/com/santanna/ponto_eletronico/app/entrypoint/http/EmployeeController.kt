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
@RequestMapping("/colaborador")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Employee", description = "End-point para gestão de funcionários")
class EmployeeController(val employeeService: EmployeeService ) {

    @GetMapping("/id")
    @Operation(summary = "Buscar funcionário pelo id")
    fun getEmployeeDataForCurrentEmployee(): ResponseEntity<EmployeeGetDto> {
        return ResponseEntity.ok(employeeService.getEmployeeById())
    }

    @PostMapping("/password/reset")
    @Operation(summary = "Reseta a senha do funcionário")
    fun resetPasswordForCurrentEmployee(@Valid @RequestBody resetPasswordDto: ResetPasswordDto): ResponseEntity<Void> {
        employeeService.resetPassword(resetPasswordDto)
        return ResponseEntity.ok().build()
    }

    @PutMapping("/password/update")
    @Operation(summary = "Alteração de senha")
    fun updatePasswordForCurrentEmployee(@Valid @RequestBody updatePasswordDto: UpdatePassword): ResponseEntity<Void> {
        employeeService.updatePassword(updatePasswordDto)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/adm")
    @Operation(summary = "Administrador busca todos os funcionários")
    fun getAllEmployeeDataAsManager( pageable: Pageable): ResponseEntity<Page<EmployeeGetDto>> {
        val employees = employeeService.getEmployeesAsManager(pageable)
        return ResponseEntity.ok(employees)
    }

    @GetMapping("/adm/busca/nome")
    @Operation(summary = "Administrador busca funcionário pelo nome")
    fun getEmployeeDataByNameAndSurnameAsManager(@RequestParam("name") name: String, @RequestParam("surname") surname: String
    ): ResponseEntity<EmployeeGetDto?> {

        val employee = employeeService.getEmployeeByNameAndSurnameAsManager(name, surname)
        return ResponseEntity.ok(employee)

    }

    @GetMapping("/adm/busca/cpf")
    @Operation(summary = "Administrador busca funcionário pelo cpf")
    fun getEmployeeDataByCpfAsManage(@Valid @RequestBody request: ManagerEmployeeRequestByCPFDto): ResponseEntity<EmployeeGetDto?> {
        val employee = employeeService.getEmployeeByCpfAsManager(request)
        return ResponseEntity.ok(employee)
    }

    @PostMapping("adm/registrar")
    @Operation(summary = "Administrador registra um funcionário")
    fun registerNewEmployeeAsManager(@Valid @RequestBody createEmployeeDto: CreateEmployeeDto
    ): ResponseEntity<EmployeeDto> {
        val employeeCreated = employeeService.registerEmployeeAsManager( createEmployeeDto)
        val uri = URI.create("/employees/${employeeCreated.id}")
        return ResponseEntity.created(uri).body(employeeCreated)
    }

    @PutMapping("/adm")
    @Operation(summary = "Administrador atualiza dados do funcionário")
    fun updateEmployeeAsManager(@Valid @RequestBody updateEmployeeRequestDto: UpdateEmployeeRequestDto): ResponseEntity<UpdateEmployeeDto> {
        val updatedEmployeeDto = employeeService.updateEmployeeAsManager(updateEmployeeRequestDto)
        return ResponseEntity.ok(updatedEmployeeDto)
    }

    @DeleteMapping("/adm")
    @Operation(summary = "Administrador apaga funcionário")
    fun deleteEmployee(@RequestBody deleteEmployeeRequestDto: DeleteEmployeeRequestDto): ResponseEntity<Void> {
        employeeService.deleteEmployeeAsManager(deleteEmployeeRequestDto)
        return ResponseEntity.noContent().build()
    }


}
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
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.util.*

@RestController
@RequestMapping("/employee")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Colaborador", description = "End-point para gestão do colaborador")
class EmployeeController(val employeeService: EmployeeService) {

    @GetMapping("/search/id")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Buscar funcionário pelo id",
        description = "Usuário tem acesso as suas própias informações.\n Requer role USER para acesso"
    )
    fun getEmployeeDataForCurrentEmployee(): ResponseEntity<EmployeeGetDto> {
        return ResponseEntity.ok(employeeService.getEmployeeById())
    }

    @PostMapping("/password/reset")
    @Operation(
        summary = "Reseta a senha do funcionário",
        description = "Uma senha provisória é gerada e enviada ao email do colaborador.\n Não requer permissão"
    )
    fun resetPasswordForCurrentEmployee(@Valid @RequestBody resetPasswordDto: ResetPasswordDto): ResponseEntity<Void> {
        employeeService.resetPassword(resetPasswordDto)
        return ResponseEntity.ok().build()
    }

    @PutMapping("/password/update")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Alteração de senha",
        description = "Uma nova senha é gerada ao confirmar a senha antiga.\n Requer role USER para acesso"
    )
    fun updatePasswordForCurrentEmployee(@Valid @RequestBody updatePasswordDto: UpdatePassword): ResponseEntity<Void> {
        employeeService.updatePassword(updatePasswordDto)
        return ResponseEntity.ok().build()
    }

    @PutMapping("/email/update")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Alteração de email",
        description = "Altera o email.\n Requer role USER para acesso"
    )
    fun updateEmailForCurrentEmployee(@Valid @RequestBody updateEmail: UpdateEmail): ResponseEntity<Void> {
        employeeService.updateEmail(updateEmail)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/search/adm/all")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(
        summary = "Administrador busca todos os funcionários",
        description = "Retorna uma lista com todos os usuários da empresa.\n Requer role MANAGER para acesso"
    )
    fun getAllEmployeeDataAsManager(pageable: Pageable): ResponseEntity<Page<EmployeeGetDto>> {
        val employees = employeeService.getEmployeesAsManager(pageable)
        return ResponseEntity.ok(employees)
    }

    @GetMapping("/search/adm/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(
        summary = "Manager busca funcionário pelo ID",
        description = "Permite que um usuário com role MANAGER busque um funcionário pelo ID.\n Requer role MANAGER para acesso"
    )
    fun getEmployeeByIdAsManager(@PathVariable id: UUID): ResponseEntity<EmployeeGetDto> {
        val employee = employeeService.getEmployeeByIdAsManager(id)
        return ResponseEntity.ok(employee)
    }


    @PostMapping("/adm/register")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(
        summary = "Administrador registra um funcionário",
        description = "Cadastra um colaborador na empresa.\n " +
                "A empresa em que está o usuário MANAGER é automaticamente atribuida ao novo colaborador.\n " +
                "Requer role MANAGER para acesso"
    )
    fun registerNewEmployeeAsManager(
        @Valid @RequestBody createEmployeeDto: CreateEmployeeDto
    ): ResponseEntity<EmployeeDto> {
        val employeeCreated = employeeService.registerEmployeeAsManager(createEmployeeDto)
        val uri = URI.create("/employees/${employeeCreated.id}")
        return ResponseEntity.created(uri).body(employeeCreated)
    }

    @PutMapping("/adm/update")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(
        summary = "Administrador atualiza dados do funcionário",
        description = "Atualiza dados de um colaborador.\n Requer role MANAGER para acesso"
    )
    fun updateEmployeeAsManager(@Valid @RequestBody updateEmployeeRequestDto: UpdateEmployeeRequestDto): ResponseEntity<UpdateEmployeeDto> {
        val updatedEmployeeDto = employeeService.updateEmployeeAsManager(updateEmployeeRequestDto)
        return ResponseEntity.ok(updatedEmployeeDto)
    }


    @PostMapping("/adm/delete")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Administrador apaga funcionário")
    fun deleteEmployee(@RequestBody deleteEmployeeRequestDto: DeleteEmployeeRequestDto): ResponseEntity<Void> {
        employeeService.deleteEmployeeAsManager(deleteEmployeeRequestDto)
        return ResponseEntity.noContent().build()
    }


}
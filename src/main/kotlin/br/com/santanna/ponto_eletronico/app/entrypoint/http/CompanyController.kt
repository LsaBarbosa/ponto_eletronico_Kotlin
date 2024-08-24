package br.com.santanna.ponto_eletronico.app.entrypoint.http

import br.com.santanna.ponto_eletronico.domain.dto.company.CompanyDTO
import br.com.santanna.ponto_eletronico.domain.dto.company.CompanyWithEmployeeCountDto
import br.com.santanna.ponto_eletronico.domain.dto.company.CreateCompanyDto
import br.com.santanna.ponto_eletronico.domain.dto.company.DeleteCompanyRequestDto
import br.com.santanna.ponto_eletronico.domain.service.CompanyService
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

@RestController
@RequestMapping("/company")
@Tag(name = "Empresa", description = "End-point para gestão da empresa")
@SecurityRequirement(name = "Bearer Authentication")
class CompanyController(private val companyService: CompanyService) {

    @GetMapping("/search/all")
    @PreAuthorize("hasRole('CTO')")
    @Operation(
        summary = "Lista todas as empresas",
        description = "Lista todas as empresas cadastradas, infromando a quantidade de funcionarios no sistema.\n Requer role ADMIN para acesso"
    )
    fun allCompanies(pageable: Pageable): ResponseEntity<Page<CompanyWithEmployeeCountDto>> {
        val companies = companyService.getAllCompanies(pageable)
        return ResponseEntity.ok(companies)
    }

    @GetMapping("/search/cnpj")
    @PreAuthorize("hasRole('CTO')")
    @Operation(
        summary = "Retorna uma empresa pelo atributo cnpj",
        description = "Retorna dados detalhados da empresa.\n Requer role ADMIN para acesso"
    )
    fun getCompanybyCNPJ(@RequestParam companyCNPJ: String): ResponseEntity<CompanyDTO> {
        val company = companyService.getCompanyByCNPJ(companyCNPJ)
        return ResponseEntity.ok().body(company)
    }

    @GetMapping("/search/name")
    @PreAuthorize("hasRole('CTO')")
    @Operation(summary = "Retorna uma empresa pelo atributo nome")
    fun getCompanybyName(@RequestParam nameCompany: String): ResponseEntity<CompanyDTO> {
        val company = companyService.getCompaniesByName(nameCompany)
        return ResponseEntity.ok().body(company)

    }

    @PostMapping
    @PreAuthorize("hasRole('CTO')")
    @Operation(summary = "Registra a empresa no sistema",
        description = "Cadastra uma empresa no sistema passando os dados do administrador da empresa.\n Requer role ADMIN para acesso")
    fun registerCompany(@Valid @RequestBody createCompanyDto: CreateCompanyDto): ResponseEntity<CompanyDTO> {
        val companyCreated = companyService.registerCompany(createCompanyDto)
        val uri: URI = URI.create("/company/${companyCreated.id}")
        return ResponseEntity.created(uri).body(companyCreated)
    }

    @PatchMapping
    @PreAuthorize("hasRole('CTO')")
    @Operation(summary = "Altera dados da empresa", description = "Altera alguns dados da empresa no sistema.\n Requer role ADMIN para acesso")
    fun updateCompany(
        @RequestParam companyCNPJ: String,
        @Valid @RequestBody companyDTO: CompanyDTO
    ): ResponseEntity<CompanyDTO> {
        val companyUpdated = companyService.updateCompany(companyCNPJ, companyDTO)
        return ResponseEntity.ok().body(companyUpdated)
    }

    @DeleteMapping
    @PreAuthorize("hasRole('CTO')")
    @Operation(summary = "Exclui a empresa do sistema pelo cnpj")
    fun deleteCompany(@RequestBody deleteCompanyRequestDto: DeleteCompanyRequestDto): ResponseEntity<Void> {
        companyService.deleteCompanyByCNPJ(deleteCompanyRequestDto)
        return ResponseEntity.noContent().build()
    }


}
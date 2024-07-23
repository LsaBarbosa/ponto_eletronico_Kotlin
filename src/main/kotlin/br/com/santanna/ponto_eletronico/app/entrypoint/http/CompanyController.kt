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
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/empresa")
@Tag(name = "Empresas", description = "End-point para gestão da empresa")
@SecurityRequirement(name = "Bearer Authentication")
class CompanyController(private val companyService: CompanyService) {

    @GetMapping
    @Operation(summary = "Lista todas as empresas")
    fun allCompanies(pageable: Pageable): ResponseEntity<Page<CompanyWithEmployeeCountDto>> {
        val companies = companyService.getAllCompanies(pageable)
        return ResponseEntity.ok(companies)
    }

    @GetMapping("/busca-cnpj")
    @Operation(summary = "Retorna a empresa específica pelo cnpj")
    fun getCompanybyCNPJ(@RequestParam companyCNPJ: String): ResponseEntity<CompanyDTO> {
        val company = companyService.getCompanyByCNPJ(companyCNPJ)
        return ResponseEntity.ok().body(company)
    }

    @GetMapping("/busca-nome-empresa")
    @Operation(summary = "Retorna a empresa específica pelo nome")
    fun getCompanybyName(@RequestParam nameCompany: String): ResponseEntity<CompanyDTO> {
        val company = companyService.getCompaniesByName(nameCompany)
        return ResponseEntity.ok().body(company)

    }

    @PostMapping
    @Operation(summary = "Registra a empresa no sistema")
    fun registerCompany(@Valid @RequestBody createCompanyDto: CreateCompanyDto): ResponseEntity<CompanyDTO> {
        val companyCreated = companyService.registerCompany(createCompanyDto)
        val uri: URI = URI.create("/company/${companyCreated.id}")
        return ResponseEntity.created(uri).body(companyCreated)
    }

    @PutMapping
    @Operation(summary = "Altera nome da empresa")
    fun updateCompany(
        @RequestParam companyCNPJ: String,
        @Valid @RequestBody companyDTO: CompanyDTO
    ): ResponseEntity<CompanyDTO> {
        val companyUpdated = companyService.updateCompany(companyCNPJ, companyDTO)
        return ResponseEntity.ok().body(companyUpdated)
    }

    @DeleteMapping
    @Operation(summary = "Exclui a empresa do sistema pelo cnpj")
    fun deleteCompany(@RequestBody deleteCompanyRequestDto: DeleteCompanyRequestDto): ResponseEntity<Void> {
        companyService.deleteCompanyByCNPJ(deleteCompanyRequestDto)
        return ResponseEntity.noContent().build()
    }


}
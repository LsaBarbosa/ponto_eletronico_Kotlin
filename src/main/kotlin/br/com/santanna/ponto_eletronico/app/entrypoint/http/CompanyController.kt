package br.com.santanna.ponto_eletronico.app.entrypoint.http

import br.com.santanna.ponto_eletronico.domain.dto.company.CompanyDTO
import br.com.santanna.ponto_eletronico.domain.dto.company.CompanyWithEmployeeCountDto
import br.com.santanna.ponto_eletronico.domain.service.CompanyService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/empresa")
class CompanyController(private val companyService: CompanyService) {

    @GetMapping
    fun allCompanies(pageable: Pageable): ResponseEntity<Page<CompanyWithEmployeeCountDto>> {
        val companies = companyService.getAllCompanies(pageable)
        return ResponseEntity.ok(companies)
    }

    @GetMapping("/busca-cnpj")
    fun getCompanybyCNPJ(@RequestParam companyCNPJ: String): ResponseEntity<CompanyDTO> {
        val company = companyService.getCompanyByCNPJ(companyCNPJ)
        return ResponseEntity.ok().body(company)
    }

    @GetMapping("/busca-nome-empresa")
    fun getCompanybyName(@RequestParam nameCompany: String): ResponseEntity<CompanyDTO> {
        val company = companyService.getCompaniesByName(nameCompany)
        return ResponseEntity.ok().body(company)

    }

    @PostMapping
    fun registerCompany(@Valid @RequestBody companyDTO: CompanyDTO): ResponseEntity<CompanyDTO> {
        val companyCreated = companyService.registerCompany(companyDTO)
        val uri: URI = URI.create("/company/${companyCreated.id}")
        return ResponseEntity.created(uri).body(companyCreated)
    }

    @PutMapping
    fun updateCompany(
        @RequestParam companyCNPJ: String,
        @Valid @RequestBody companyDTO: CompanyDTO
    ): ResponseEntity<CompanyDTO> {
        val companyUpdated = companyService.updateCompany(companyCNPJ, companyDTO)
        return ResponseEntity.ok().body(companyUpdated)
    }

    @DeleteMapping
    fun deleteCompany(@RequestParam ("companyCNPJ") companyCNPJ: String): ResponseEntity<Void> {
        companyService.deleteCompanyByCNPJ(companyCNPJ)
        return ResponseEntity.noContent().build()
    }


}
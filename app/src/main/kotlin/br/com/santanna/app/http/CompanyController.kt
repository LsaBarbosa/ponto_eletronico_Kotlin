package br.com.santanna.app.http

import br.com.santanna.domain.dto.company.CompanyDTO
import br.com.santanna.domain.dto.company.CompanyWithEmployeesDto
import br.com.santanna.domain.service.CompanyService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/empresa")
class CompanyController(private val companyService: CompanyService) {

    @GetMapping
    fun allCompanies(): List<CompanyWithEmployeesDto> {
        return companyService.getAllCompanies()
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
    fun registerCompany(@RequestBody companyDTO: CompanyDTO): ResponseEntity<CompanyDTO> {
        val companyCreated = companyService.registerCompany(companyDTO)
        val uri: URI = URI.create("/company/${companyCreated.id}")
        return ResponseEntity.created(uri).body(companyCreated)
    }

    @PutMapping
    fun updateCompany(
        @RequestParam companyCNPJ: String,
        @RequestBody companyDTO: CompanyDTO
    ): ResponseEntity<CompanyDTO> {
        val companyUpdated = companyService.updateCompany(companyCNPJ, companyDTO)
        return ResponseEntity.ok().body(companyUpdated)
    }

    @DeleteMapping
    fun deleteCompany(@RequestParam nameCompany: String): ResponseEntity<Void> {
        companyService.deleteCompany(nameCompany)
        return ResponseEntity.noContent().build()
    }
}
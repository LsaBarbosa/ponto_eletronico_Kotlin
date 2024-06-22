package br.com.santanna.ponto_eletronico.controller

import br.com.santanna.ponto_eletronico.model.Company
import br.com.santanna.ponto_eletronico.model.dto.company.CompanyDTO
import br.com.santanna.ponto_eletronico.service.CompanyService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/empresa")
class CompanyController (private val companyService: CompanyService) {

    @GetMapping
    fun allCompanies():List<Company>{
        return companyService.getAllCompanies()
    }


    @GetMapping("/busca-cnpj")
    fun getCompanybyCNPJ(@RequestParam companyCNPJ:String): ResponseEntity<CompanyDTO>{
        return try{
            val company = companyService.getCompanyByCNPJ(companyCNPJ)
            ResponseEntity.ok().body(company)
        }catch (ex:Exception){
            ResponseEntity.notFound().build()
        }
    }
        @GetMapping("/busca-nome-empresa")
    fun getCompanybyName(@RequestParam nameCompany:String): ResponseEntity<CompanyDTO>{
        return try{
            val company = companyService.getCompaniesByName(nameCompany)
            ResponseEntity.ok().body(company)
        }catch (ex:Exception){
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun registerCompany(@RequestBody companyDTO: CompanyDTO): ResponseEntity<CompanyDTO>{
        return try{
            val companyCreated = companyService.registerCompany(companyDTO)
            val uri:URI = URI.create("/company/${companyCreated.id}")
            ResponseEntity.created(uri).body(companyCreated)
        }catch (ex:Exception){
            ResponseEntity.badRequest().build()
        }
    }


    @PutMapping
    fun updateCompany(@RequestParam companyCNPJ: String,@RequestBody companyDTO: CompanyDTO): ResponseEntity<CompanyDTO> {
        return try {
            val companyUpdated = companyService.updateCompany(companyCNPJ,companyDTO)
            ResponseEntity.ok().body(companyUpdated)
        } catch (ex: Exception) {
            return ResponseEntity.notFound().build()
        } catch (ex: IllegalArgumentException) {
            return ResponseEntity.badRequest().build()
        }
    }

    @DeleteMapping
    fun deleteCompany(@RequestParam nameCompany: String): ResponseEntity<Void>{
        return try {
            companyService.deleteCompany(nameCompany)
            ResponseEntity.noContent().build()
        } catch (ex: Exception) {
            return ResponseEntity.notFound().build()
        }
    }
}
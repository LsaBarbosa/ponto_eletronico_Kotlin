package br.com.santanna.ponto_eletronico.repository

import br.com.santanna.ponto_eletronico.model.Company
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyRepository: JpaRepository<Company, Long>{
     fun findByCompanyCNPJ(companyCNPJ: String?): Company?
     fun existsByCompanyCNPJ(companyCNPJ: String?): Boolean
     fun findByNameCompanyContainsIgnoreCase(nameCompany: String?): Company
     fun existsByNameCompanyIgnoreCase(nameCompany: String?): Boolean

}

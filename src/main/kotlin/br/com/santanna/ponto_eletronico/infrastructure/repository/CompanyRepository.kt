package br.com.santanna.ponto_eletronico.infrastructure.repository

import br.com.santanna.ponto_eletronico.domain.entity.Company
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyRepository: JpaRepository<Company, Long>{
     fun findByCompanyCNPJ(companyCNPJ: String?): Company?

     fun findByNameCompanyContainsIgnoreCase(nameCompany: String?): Company?
     fun existsByNameCompanyIgnoreCase(nameCompany: String?): Boolean

}

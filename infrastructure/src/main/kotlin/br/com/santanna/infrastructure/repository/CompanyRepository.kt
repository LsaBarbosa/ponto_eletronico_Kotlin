package br.com.santanna.infrastructure.repository

import br.com.santanna.infrastructure.model.CompanyModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyRepository: JpaRepository<CompanyModel, Long>{
     fun findByCompanyCNPJ(companyCNPJ: String?): CompanyModel?

     fun findByNameCompanyContainsIgnoreCase(nameCompany: String?): CompanyModel?
     fun existsByNameCompanyIgnoreCase(nameCompany: String?): Boolean

}

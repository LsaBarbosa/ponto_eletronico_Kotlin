package br.com.santanna.ponto_eletronico.domain.dataprovider

import br.com.santanna.ponto_eletronico.domain.entity.company.Company
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CompanyDataprovider {
    fun findByCompanyCNPJ(companyCNPJ: String?): Company?
    fun findByNameCompanyContainsIgnoreCase(nameCompany: String?): Company?
    fun existsByNameCompanyIgnoreCase(nameCompany: String?): Boolean
    fun deleteByCompanyCNPJ(companyCNPJ: String)
    fun findAll(pageable: Pageable): Page<Company>
    fun save(company: Company): Company?
}
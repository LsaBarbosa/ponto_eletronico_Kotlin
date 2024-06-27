package br.com.santanna.domain.dataprovider

import br.com.santanna.domain.entity.Company


interface CompanyDataprovider {
    fun findByCompanyCNPJ(companyCNPJ: String?): Company?
    fun findByNameCompanyContainsIgnoreCase(nameCompany: String?): Company?
    fun existsByNameCompanyIgnoreCase(nameCompany: String?): Boolean
    fun deleteByNameCompany(nameCompany: String?)
    fun findAll(): List<Company>
    fun save(company: Company): Company?
}
package br.com.santanna.ponto_eletronico.domain.dataprovider

import br.com.santanna.ponto_eletronico.domain.entity.Company

interface CompanyDataprovider {
    fun findByCompanyCNPJ(companyCNPJ: String?): Company?
    fun findByNameCompanyContainsIgnoreCase(nameCompany: String?): Company?
    fun existsByNameCompanyIgnoreCase(nameCompany: String?): Boolean
    fun deleteByNameCompany(nameCompany: String?)
    fun findAll(): MutableList<Company>
    fun save(company: Company): Company?
}
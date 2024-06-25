package br.com.santanna.ponto_eletronico.repository.dataProvider

import br.com.santanna.ponto_eletronico.model.Company

interface CompanyDataprovider {
    fun findByCompanyCNPJ(companyCNPJ: String?): Company?
    fun findByNameCompanyContainsIgnoreCase(nameCompany: String?): Company?
    fun existsByNameCompanyIgnoreCase(nameCompany: String?): Boolean
    fun deleteByNameCompany(nameCompany: String?)
    fun findAll(): MutableList<Company>
    fun save(company: Company): Company?
}
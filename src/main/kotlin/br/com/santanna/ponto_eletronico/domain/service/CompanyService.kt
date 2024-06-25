package br.com.santanna.ponto_eletronico.domain.service

import br.com.santanna.ponto_eletronico.domain.dto.company.CompanyDTO
import br.com.santanna.ponto_eletronico.domain.dto.company.CompanyWithEmployeesDto

interface CompanyService {
    fun getAllCompanies(): List<CompanyWithEmployeesDto>
    fun getCompanyByCNPJ(companyCNPJ: String?): CompanyDTO
    fun getCompaniesByName(nameCompany: String): CompanyDTO
    fun registerCompany(companyDto: CompanyDTO): CompanyDTO
    fun updateCompany(companyCNPJ: String?, companyDto: CompanyDTO): CompanyDTO
    fun deleteCompany(nameCompany: String)
}
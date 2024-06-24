package br.com.santanna.ponto_eletronico.service

import br.com.santanna.ponto_eletronico.model.dto.company.CompanyDTO
import br.com.santanna.ponto_eletronico.model.dto.company.CompanyWithEmployeesDto

interface ComanyService {
    fun getAllCompanies(): List<CompanyWithEmployeesDto>
    fun getCompanyByCNPJ(companyCNPJ: String?): CompanyDTO
    fun getCompaniesByName(nameCompany: String): CompanyDTO
    fun registerCompany(companyDto: CompanyDTO): CompanyDTO
    fun updateCompany(companyCNPJ: String?, companyDto: CompanyDTO): CompanyDTO
    fun deleteCompany(nameCompany: String)
}
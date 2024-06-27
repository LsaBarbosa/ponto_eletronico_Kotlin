package br.com.santanna.domain.service

import br.com.santanna.domain.dto.company.CompanyDTO
import br.com.santanna.domain.dto.company.CompanyWithEmployeesDto
import org.springframework.stereotype.Service


interface CompanyService {
    fun getAllCompanies(): List<CompanyWithEmployeesDto>
    fun getCompanyByCNPJ(companyCNPJ: String?): CompanyDTO
    fun getCompaniesByName(nameCompany: String): CompanyDTO
    fun registerCompany(companyDto: CompanyDTO): CompanyDTO
    fun updateCompany(companyCNPJ: String?, companyDto: CompanyDTO): CompanyDTO
    fun deleteCompany(nameCompany: String)
}
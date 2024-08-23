package br.com.santanna.ponto_eletronico.domain.service

import br.com.santanna.ponto_eletronico.domain.dto.company.CompanyDTO
import br.com.santanna.ponto_eletronico.domain.dto.company.CompanyWithEmployeeCountDto
import br.com.santanna.ponto_eletronico.domain.dto.company.CreateCompanyDto
import br.com.santanna.ponto_eletronico.domain.dto.company.DeleteCompanyRequestDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CompanyService {
    fun getAllCompanies(pageable: Pageable): Page<CompanyWithEmployeeCountDto>
    fun getCompanyByCNPJ(companyCNPJ: String): CompanyDTO
    fun getCompaniesByName(nameCompany: String): CompanyDTO
    fun registerCompany(createCompanyDto: CreateCompanyDto): CompanyDTO
    fun updateCompany(companyCNPJ: String, companyDto: CompanyDTO): CompanyDTO
    fun deleteCompanyByCNPJ(deleteCompanyRequestDto: DeleteCompanyRequestDto)
}
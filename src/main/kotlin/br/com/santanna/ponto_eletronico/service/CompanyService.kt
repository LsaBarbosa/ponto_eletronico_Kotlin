package br.com.santanna.ponto_eletronico.service

import br.com.santanna.ponto_eletronico.model.Company
import br.com.santanna.ponto_eletronico.model.dto.company.CompanyDTO
import br.com.santanna.ponto_eletronico.repository.CompanyRepository
import br.com.santanna.ponto_eletronico.util.company.CompanyUtils
import org.springframework.stereotype.Service

@Service
class CompanyService(private val companyRepository: CompanyRepository) {
    fun getAllCompanies(): List<Company> {
        return companyRepository.findAll()
    }

    fun getCompanyByCNPJ(companyCNPJ: String?): CompanyDTO {
        val company = companyRepository.findByCompanyCNPJ(companyCNPJ)
            ?: throw Exception("Company not found with CNPJ: $companyCNPJ")
        return CompanyUtils.convertToDtoCompany(company)
    }

    fun getCompaniesByName(nameCompany: String): CompanyDTO {
        val company = companyRepository.findByNameCompanyContainsIgnoreCase(nameCompany)
        return CompanyUtils.convertToDtoCompany(company)
    }

    fun registerCompany(companyDto: CompanyDTO): CompanyDTO {
        if (companyRepository.existsByNameCompanyIgnoreCase(companyDto.nameCompany ?: "")) {
            throw Exception("Company with the same name already exists.")
        }
        val companyEntity = CompanyUtils.convertToEntity(companyDto)
        val savedCompanyEntity = companyRepository.save(companyEntity)
        return CompanyUtils.convertToDto(savedCompanyEntity)
    }

    fun updateCompany(companyCNPJ: String?, companyDto: CompanyDTO): CompanyDTO {
        val existingCompany = companyRepository.findByCompanyCNPJ(companyCNPJ)
            ?: throw Exception("Company not found with CNPJ: $companyCNPJ")

        existingCompany.nameCompany = companyDto.nameCompany ?: existingCompany.nameCompany
        val updatedCompanyEntity = companyRepository.save(existingCompany)
        return CompanyUtils.convertToDto(updatedCompanyEntity)
    }

    fun deleteCompany(nameCompany: String) {
        CompanyUtils.deleteCompany(companyRepository, nameCompany)
    }
}

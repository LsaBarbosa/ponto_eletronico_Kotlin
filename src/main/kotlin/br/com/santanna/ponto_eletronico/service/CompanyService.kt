package br.com.santanna.ponto_eletronico.service

import br.com.santanna.ponto_eletronico.model.Company
import br.com.santanna.ponto_eletronico.model.dto.company.CompanyDTO
import br.com.santanna.ponto_eletronico.repository.CompanyRepository
import org.springframework.stereotype.Service

@Service
class CompanyService(private val companyRepository: CompanyRepository) {

    fun getAllCompanies(): List<Company> {
        return companyRepository.findAll()
    }

    fun getCompanyByCNPJ(companyCNPJ: String?): Company {
        return companyRepository.findByCompanyCNPJ(companyCNPJ) ?: throw Exception("Company not found")
    }

    fun getCompaniesByName(nameCompany: String?): Company {
        return companyRepository.findByNameCompanyContainsIgnoreCase(nameCompany)

    }

    fun registerCompany(companyDto: CompanyDTO):CompanyDTO {
        if (companyRepository.existsByNameCompanyIgnoreCase(companyDto.nameCompany)) {
            throw  Exception("Employee with the same name already exists.")
        }
        val companyEntity = convertToEntity(companyDto)
        val savedCompanyEntity = companyRepository.save(companyEntity)

        return convertToDto(savedCompanyEntity)
    }

    fun updateCompany(companyCNPJ: String?,companyDto: CompanyDTO):CompanyDTO{
        val existingCompany = companyRepository.findByCompanyCNPJ(companyCNPJ)
            ?: throw Exception()

        existingCompany.nameCompany = companyDto.nameCompany ?: existingCompany.nameCompany
        val updateCompanyEntity = companyRepository.save(existingCompany)
        return convertToDto(updateCompanyEntity)
    }

    fun deleteCompany(nameCompany: String){
        val companyToDelete = companyRepository.findByNameCompanyContainsIgnoreCase(nameCompany)
        companyRepository.delete(companyToDelete)
    }

    private fun convertToEntity(companyDto: CompanyDTO): Company {
        return Company(
            id = companyDto.id,
            nameCompany = companyDto.nameCompany,
            companyCNPJ = companyDto.companyCNPJ
            )
    }

    private fun convertToDto(company: Company):CompanyDTO {
        return CompanyDTO(
            id = company.id,
                nameCompany = company.nameCompany,
                companyCNPJ = company.companyCNPJ

        )
    }
}

package br.com.santanna.ponto_eletronico.service.impl

import br.com.santanna.ponto_eletronico.model.dto.company.CompanyDTO
import br.com.santanna.ponto_eletronico.model.dto.company.CompanyWithEmployeesDto
import br.com.santanna.ponto_eletronico.model.dto.employee.EmployeeDto
import br.com.santanna.ponto_eletronico.repository.CompanyRepository
import br.com.santanna.ponto_eletronico.service.ComanyService
import br.com.santanna.ponto_eletronico.util.company.CompanyUtils
import org.springframework.stereotype.Service

@Service
class CompanyServiceImpl(private val companyRepository: CompanyRepository): ComanyService {

    override fun getAllCompanies(): List<CompanyWithEmployeesDto> {
        return companyRepository.findAll().map { company ->
            CompanyWithEmployeesDto(
                id = company.id,
                nameCompany = company.nameCompany,
                companyCNPJ = company.companyCNPJ,
                employees = company.employees.map { employee ->
                    EmployeeDto(
                        id = employee?.id,
                        name = employee?.name,
                        surname = employee?.surname,
                        salary = employee?.salary,
                        position = employee?.position
                    )
                }
            )
        }
    }

    override fun getCompanyByCNPJ(companyCNPJ: String?): CompanyDTO {
        val company = companyRepository.findByCompanyCNPJ(companyCNPJ)
            ?: throw Exception("Company not found with CNPJ: $companyCNPJ")
        return CompanyUtils.convertToDtoCompany(company)
    }

    override fun getCompaniesByName(nameCompany: String): CompanyDTO {
        val company = companyRepository.findByNameCompanyContainsIgnoreCase(nameCompany)
        return CompanyUtils.convertToDtoCompany(company)
    }

    override fun registerCompany(companyDto: CompanyDTO): CompanyDTO {
        if (companyRepository.existsByNameCompanyIgnoreCase(companyDto.nameCompany ?: "")) {
            throw Exception("Company with the same name already exists.")
        }
        val companyEntity = CompanyUtils.convertToEntity(companyDto)
        val savedCompanyEntity = companyRepository.save(companyEntity)
        return CompanyUtils.convertToDto(savedCompanyEntity)
    }

    override fun updateCompany(companyCNPJ: String?, companyDto: CompanyDTO): CompanyDTO {
        val existingCompany = companyRepository.findByCompanyCNPJ(companyCNPJ)
            ?: throw Exception("Company not found with CNPJ: $companyCNPJ")

        existingCompany.nameCompany = companyDto.nameCompany ?: existingCompany.nameCompany
        val updatedCompanyEntity = companyRepository.save(existingCompany)
        return CompanyUtils.convertToDto(updatedCompanyEntity)
    }

    override fun deleteCompany(nameCompany: String) {
        val companyToDelete = companyRepository.findByNameCompanyContainsIgnoreCase(nameCompany)
        companyRepository.delete(companyToDelete)
    }
}
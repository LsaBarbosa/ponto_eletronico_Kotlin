package br.com.santanna.ponto_eletronico.service

import br.com.santanna.ponto_eletronico.model.Company
import br.com.santanna.ponto_eletronico.model.Employee
import br.com.santanna.ponto_eletronico.model.dto.company.CompanyDTO
import br.com.santanna.ponto_eletronico.model.dto.employee.SimpleEmployeeDto
import br.com.santanna.ponto_eletronico.repository.CompanyRepository
import org.springframework.stereotype.Service

@Service
class CompanyService(private val companyRepository: CompanyRepository) {

    fun getAllCompanies(): List<Company> {
        return companyRepository.findAll()
    }

    fun getCompanyByCNPJ(companyCNPJ: String?): CompanyDTO {
       val company =companyRepository.findByCompanyCNPJ(companyCNPJ) ?: throw Exception("Company not found")
        return convertToDtoCompany(company)
    }

    fun getCompaniesByName(nameCompany: String?): CompanyDTO {
        val company = companyRepository.findByNameCompanyContainsIgnoreCase(nameCompany)
    return convertToDtoCompany(company)
    }

    fun registerCompany(companyDto: CompanyDTO): CompanyDTO {
        if (companyRepository.existsByNameCompanyIgnoreCase(companyDto.nameCompany)) {
            throw Exception("Employee with the same name already exists.")
        }
        val companyEntity = convertToEntity(companyDto)
        val savedCompanyEntity = companyRepository.save(companyEntity)

        return convertToDto(savedCompanyEntity)
    }

    fun updateCompany(companyCNPJ: String?, companyDto: CompanyDTO): CompanyDTO {
        val existingCompany = companyRepository.findByCompanyCNPJ(companyCNPJ)
            ?: throw Exception()

        existingCompany.nameCompany = companyDto.nameCompany ?: existingCompany.nameCompany
        val updateCompanyEntity = companyRepository.save(existingCompany)
        return convertToDto(updateCompanyEntity)
    }

    fun deleteCompany(nameCompany: String) {
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

    private fun convertToDto(company: Company): CompanyDTO {
        return CompanyDTO(
            id = company.id,
            nameCompany = company.nameCompany,
            companyCNPJ = company.companyCNPJ

        )
    }

    private fun convertToDtoCompany(company: Company): CompanyDTO {
        // Converter os funcionários para SimpleEmployeeDto
        val simpleEmployeeDtos = company.employees.mapNotNull { convertToSimpleEmployeeDto(it) }

        return CompanyDTO(
            id = company.id,
            nameCompany = company.nameCompany,
            companyCNPJ = company.companyCNPJ,
            employees = simpleEmployeeDtos
        )
    }


    private fun convertToSimpleEmployeeDto(employee: Employee?): SimpleEmployeeDto? {
        return employee?.let {
            SimpleEmployeeDto(
                id = it.id,
                name = it.name,
                surname = it.surname,
                salary = it.salary,
                position = it.position
            )
        }
    }
}

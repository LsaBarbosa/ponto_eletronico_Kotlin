package br.com.santanna.ponto_eletronico.domain.service.impl

import br.com.santanna.ponto_eletronico.domain.entity.Company
import br.com.santanna.ponto_eletronico.domain.entity.Employee
import br.com.santanna.ponto_eletronico.domain.dto.company.CompanyDTO
import br.com.santanna.ponto_eletronico.domain.dto.company.CompanyWithEmployeesDto
import br.com.santanna.ponto_eletronico.domain.dto.employee.EmployeeDto
import br.com.santanna.ponto_eletronico.domain.dto.employee.SimpleEmployeeDto
import br.com.santanna.ponto_eletronico.domain.dataprovider.CompanyDataprovider
import br.com.santanna.ponto_eletronico.domain.service.CompanyService
import br.com.santanna.ponto_eletronico.app.handler.model.DataIntegrityViolationException
import org.springframework.stereotype.Service

@Service
class CompanyServiceImpl(private val companyDataProvider: CompanyDataprovider) : CompanyService {

    override fun getAllCompanies(): List<CompanyWithEmployeesDto> {
        return companyDataProvider.findAll().map { company ->
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
        val company = companyDataProvider.findByCompanyCNPJ(companyCNPJ)
        return  convertToDtoCompany(company)
    }

    override fun getCompaniesByName(nameCompany: String): CompanyDTO {
        val company = companyDataProvider.findByNameCompanyContainsIgnoreCase(nameCompany)
        return  convertToDtoCompany(company)
    }

    override fun registerCompany(companyDto: CompanyDTO): CompanyDTO {
        val isExistCompany =companyDataProvider.existsByNameCompanyIgnoreCase(companyDto.nameCompany)
        if (isExistCompany) {
            throw DataIntegrityViolationException("Empresa já existe")
        }
        val companyEntity =  convertToEntity(companyDto)
        val savedCompanyEntity = companyDataProvider.save(companyEntity)
        return  convertToDto(savedCompanyEntity)
    }

    override fun updateCompany(companyCNPJ: String?, companyDto: CompanyDTO): CompanyDTO {
        val existingCompany = companyDataProvider.findByCompanyCNPJ(companyCNPJ)
        existingCompany?.nameCompany = companyDto.nameCompany ?: existingCompany?.nameCompany
        val updatedCompanyEntity = companyDataProvider.save(existingCompany!!)
        return  convertToDto(updatedCompanyEntity)
    }

    override fun deleteCompany(nameCompany: String) {
        companyDataProvider.deleteByNameCompany(nameCompany)
    }

    fun convertToEntity(companyDto: CompanyDTO): Company {
        return Company(
            id = companyDto.id,
            nameCompany = companyDto.nameCompany,
            companyCNPJ = companyDto.companyCNPJ
        )
    }

    fun convertToDto(company: Company?): CompanyDTO {
        return CompanyDTO(
            id = company?.id,
            nameCompany = company?.nameCompany,
            companyCNPJ = company?.companyCNPJ
        )
    }

    fun convertToDtoCompany(company: Company?): CompanyDTO {
        val simpleEmployeeDtos = company?.employees?.mapNotNull { convertToSimpleEmployeeDto(it) }

        return CompanyDTO(
            id = company?.id,
            nameCompany = company?.nameCompany,
            companyCNPJ = company?.companyCNPJ,
            employees = simpleEmployeeDtos
        )
    }

    fun convertToSimpleEmployeeDto(employee: Employee?): SimpleEmployeeDto? {
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
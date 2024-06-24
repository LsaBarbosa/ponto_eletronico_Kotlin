package br.com.santanna.ponto_eletronico.util.company

import br.com.santanna.ponto_eletronico.model.Company
import br.com.santanna.ponto_eletronico.model.Employee
import br.com.santanna.ponto_eletronico.model.dto.company.CompanyDTO
import br.com.santanna.ponto_eletronico.model.dto.employee.SimpleEmployeeDto
import br.com.santanna.ponto_eletronico.repository.CompanyRepository
import br.com.santanna.ponto_eletronico.repository.EmployeeRepository

object CompanyUtils {
    fun convertToEntity(companyDto: CompanyDTO): Company {
        return Company(
            id = companyDto.id,
            nameCompany = companyDto.nameCompany,
            companyCNPJ = companyDto.companyCNPJ
        )
    }

    fun convertToDto(company: Company): CompanyDTO {
        return CompanyDTO(
            id = company.id,
            nameCompany = company.nameCompany,
            companyCNPJ = company.companyCNPJ
        )
    }

    fun convertToDtoCompany(company: Company): CompanyDTO {
        val simpleEmployeeDtos = company.employees.mapNotNull { convertToSimpleEmployeeDto(it) }

        return CompanyDTO(
            id = company.id,
            nameCompany = company.nameCompany,
            companyCNPJ = company.companyCNPJ,
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
package br.com.santanna.ponto_eletronico.domain.dto.todto

import br.com.santanna.ponto_eletronico.domain.dto.company.AddressDTO
import br.com.santanna.ponto_eletronico.domain.dto.company.CompanyDTO
import br.com.santanna.ponto_eletronico.domain.dto.company.CompanyWithEmployeeCountDto
import br.com.santanna.ponto_eletronico.domain.dto.employee.SimpleEmployeeDto
import br.com.santanna.ponto_eletronico.domain.entity.company.Company
import br.com.santanna.ponto_eletronico.domain.entity.employee.Employee
import org.springframework.stereotype.Component

@Component
class CompanyToDto {


    fun convertToDto(company: Company?): CompanyDTO {
        return CompanyDTO(
            id = company?.id,
            nameCompany = company?.nameCompany,
            companyCNPJ = company?.companyCNPJ,
            address = company?.address?.let {
                AddressDTO(
                    street = it.street,
                    city = it.city,
                    state = it.state,
                    number = it.number,
                    postalCode = it.postalCode
                )
            },
            employees = company?.employees?.map { SimpleEmployeeDto(it?.id, it?.name) }
        )
    }

    fun convertToDtoCompany(company: Company?): CompanyDTO {
        val simpleEmployeeDtos = company?.employees?.mapNotNull { convertToSimpleEmployeeDto(it) }

        return CompanyDTO(
            id = company?.id,
            nameCompany = company?.nameCompany,
            companyCNPJ = company?.companyCNPJ,
            employees = simpleEmployeeDtos,
            address = company?.address?.let {
                AddressDTO(
                    street = it.street,
                    city = it.city,
                    state = it.state,
                    number = it.number,
                    postalCode = it.postalCode
                )
            },
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
    fun convertToCompanyWithEmployeeCountDto(company: Company): CompanyWithEmployeeCountDto {
        val employeeCount = company.employees.size.toLong()
        return CompanyWithEmployeeCountDto(
            nameCompany = company.nameCompany ?: "",
            companyCNPJ = company.companyCNPJ ?: "",
            employeeCount = employeeCount
        )
    }
}
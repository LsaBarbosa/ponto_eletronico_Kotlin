package br.com.santanna.ponto_eletronico.domain.dto.todto


import br.com.santanna.ponto_eletronico.domain.dto.company.AddressDTO
import br.com.santanna.ponto_eletronico.domain.dto.company.CompanyDTO
import br.com.santanna.ponto_eletronico.domain.dto.company.CompanyWithEmployeeCountDto
import br.com.santanna.ponto_eletronico.domain.entity.company.Company
import org.springframework.stereotype.Component

@Component
class CompanyToDto {


    fun convertToDto(company: Company): CompanyDTO {
        return CompanyDTO(
            id = company.id,
            nameCompany = company.nameCompany,
            companyCNPJ = company.companyCNPJ,
            address = company.address.let {
                AddressDTO(
                    street = it.street ?: "",
                    city = it.city ?: "",
                    state = it.state ?: "",
                    number = it.number ?: "",
                    postalCode = it.postalCode!!
                )
            },
        )
    }

    fun convertToCompanyWithEmployeeCountDto(company: Company): CompanyWithEmployeeCountDto {
        val employeeCount = company.employees.size.toLong()
        return CompanyWithEmployeeCountDto(
            nameCompany = company.nameCompany,
            companyCNPJ = company.companyCNPJ,
            employeeCount = employeeCount
        )
    }
}
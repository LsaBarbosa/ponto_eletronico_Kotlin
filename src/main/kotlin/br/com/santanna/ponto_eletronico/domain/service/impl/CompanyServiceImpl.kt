package br.com.santanna.ponto_eletronico.domain.service.impl

import br.com.santanna.ponto_eletronico.domain.entity.Company
import br.com.santanna.ponto_eletronico.domain.entity.Employee
import br.com.santanna.ponto_eletronico.domain.dto.company.CompanyDTO
import br.com.santanna.ponto_eletronico.domain.dto.company.CompanyWithEmployeeCountDto
import br.com.santanna.ponto_eletronico.domain.dto.employee.SimpleEmployeeDto
import br.com.santanna.ponto_eletronico.domain.dataprovider.CompanyDataprovider
import br.com.santanna.ponto_eletronico.domain.service.CompanyService
import br.com.santanna.ponto_eletronico.app.handler.model.DataIntegrityViolationException
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class CompanyServiceImpl(private val companyDataProvider: CompanyDataprovider) : CompanyService {

    override fun getAllCompanies(pageable: Pageable): Page<CompanyWithEmployeeCountDto> {
        val companies = companyDataProvider.findAll(pageable)
        return companies.map { convertToCompanyWithEmployeeCountDto(it) }
    }

    override fun getCompanyByCNPJ(companyCNPJ: String?): CompanyDTO {
        val company = companyDataProvider.findByCompanyCNPJ(companyCNPJ)
        return  convertToDtoCompany(company)
    }

    override fun getCompaniesByName(nameCompany: String): CompanyDTO {
        val company = companyDataProvider.findByNameCompanyContainsIgnoreCase(nameCompany)
        return  convertToDtoCompany(company)
    }

    @Transactional
    override fun registerCompany(companyDto: CompanyDTO): CompanyDTO {
        val isExistCompany =companyDataProvider.existsByNameCompanyIgnoreCase(companyDto.nameCompany)
        if (isExistCompany) {
            throw DataIntegrityViolationException("Empresa já existe")
        }
        val companyEntity =  convertToEntity(companyDto)
        val savedCompanyEntity = companyDataProvider.save(companyEntity)
        return  convertToDto(savedCompanyEntity)
    }

    @Transactional
    override fun updateCompany(companyCNPJ: String?, companyDto: CompanyDTO): CompanyDTO {
        val existingCompany = companyDataProvider.findByCompanyCNPJ(companyCNPJ)
        existingCompany?.nameCompany = companyDto.nameCompany ?: existingCompany?.nameCompany
        val updatedCompanyEntity = companyDataProvider.save(existingCompany!!)
        return  convertToDto(updatedCompanyEntity)
    }

    @Transactional
    override fun deleteCompanyByCNPJ(companyCNPJ: String) {
        companyDataProvider.deleteByCompanyCNPJ(companyCNPJ)
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
    private fun convertToCompanyWithEmployeeCountDto(company: Company): CompanyWithEmployeeCountDto {
        val employeeCount = company.employees.size.toLong()
        return CompanyWithEmployeeCountDto(
            nameCompany = company.nameCompany ?: "",
            companyCNPJ = company.companyCNPJ ?: "",
            employeeCount = employeeCount
        )
    }
}
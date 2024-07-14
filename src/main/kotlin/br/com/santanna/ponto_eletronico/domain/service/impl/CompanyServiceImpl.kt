package br.com.santanna.ponto_eletronico.domain.service.impl

import br.com.santanna.ponto_eletronico.domain.entity.Company
import br.com.santanna.ponto_eletronico.domain.entity.Employee
import br.com.santanna.ponto_eletronico.domain.dto.company.CompanyDTO
import br.com.santanna.ponto_eletronico.domain.dto.company.CompanyWithEmployeeCountDto
import br.com.santanna.ponto_eletronico.domain.dto.employee.SimpleEmployeeDto
import br.com.santanna.ponto_eletronico.domain.dataprovider.CompanyDataprovider
import br.com.santanna.ponto_eletronico.domain.service.CompanyService
import br.com.santanna.ponto_eletronico.app.handler.model.DataIntegrityViolationException
import br.com.santanna.ponto_eletronico.app.handler.model.ObjectNotFoundException
import br.com.santanna.ponto_eletronico.domain.dataprovider.EmployeeDataProvider
import br.com.santanna.ponto_eletronico.domain.dto.company.CreateCompanyDto
import br.com.santanna.ponto_eletronico.domain.dto.company.DeleteCompanyRequestDto
import br.com.santanna.ponto_eletronico.domain.entity.EmployeeRole
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class CompanyServiceImpl(private val companyDataProvider: CompanyDataprovider,  private val employeeDataProvider: EmployeeDataProvider) : CompanyService {

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
    override fun registerCompany(createCompanyDto: CreateCompanyDto): CompanyDTO {
        val isExistCompany = companyDataProvider.existsByNameCompanyIgnoreCase(createCompanyDto.nameCompany)
        if (isExistCompany) {
            throw DataIntegrityViolationException("Empresa já existe")
        }

        val companyEntity = Company(
            nameCompany = createCompanyDto.nameCompany,
            companyCNPJ = createCompanyDto.companyCNPJ
        )

        val savedCompanyEntity = companyDataProvider.save(companyEntity)

        val encryptedPassword = BCryptPasswordEncoder().encode(createCompanyDto.managerPasswords)

        val managerEntity = Employee(
            name = createCompanyDto.managerName,
            surname = createCompanyDto.managerSurname,
            position = createCompanyDto.managerPosition,
            cpf = createCompanyDto.managerCpf,
            role = EmployeeRole.MANAGER,
            passwords = encryptedPassword,
            company = savedCompanyEntity
        )

        employeeDataProvider.save(managerEntity)

        return convertToDto(savedCompanyEntity)
    }

    @Transactional
    override fun updateCompany(companyCNPJ: String?, companyDto: CompanyDTO): CompanyDTO {
        val existingCompany = companyDataProvider.findByCompanyCNPJ(companyCNPJ)
        existingCompany?.nameCompany = companyDto.nameCompany ?: existingCompany?.nameCompany
        val updatedCompanyEntity = companyDataProvider.save(existingCompany!!)
        return  convertToDto(updatedCompanyEntity)
    }

    @Transactional
    override fun deleteCompanyByCNPJ(deleteCompanyRequestDto: DeleteCompanyRequestDto) {
        companyDataProvider.findByCompanyCNPJ(deleteCompanyRequestDto.companyCNPJ)
            ?: throw ObjectNotFoundException("Company not found with CNPJ: ${deleteCompanyRequestDto.companyCNPJ}")

        val employee = employeeDataProvider.findCpf(deleteCompanyRequestDto.employeeCpf)
            ?: throw ObjectNotFoundException("Employee not found with CPF: ${deleteCompanyRequestDto.employeeCpf}")

        val encryptedPassword = BCryptPasswordEncoder().matches(deleteCompanyRequestDto.passwords, employee.password)
        if (!encryptedPassword) {
            throw IllegalArgumentException("Invalid password")
        }

        companyDataProvider.deleteByCompanyCNPJ(deleteCompanyRequestDto.companyCNPJ)
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
package br.com.santanna.ponto_eletronico.domain.service.impl

import br.com.santanna.ponto_eletronico.app.handler.model.DataIntegrityViolationException
import br.com.santanna.ponto_eletronico.app.handler.model.ObjectNotFoundException
import br.com.santanna.ponto_eletronico.domain.dataprovider.CompanyDataprovider
import br.com.santanna.ponto_eletronico.domain.dataprovider.EmployeeDataProvider
import br.com.santanna.ponto_eletronico.domain.dto.company.CompanyDTO
import br.com.santanna.ponto_eletronico.domain.dto.company.CompanyWithEmployeeCountDto
import br.com.santanna.ponto_eletronico.domain.dto.company.CreateCompanyDto
import br.com.santanna.ponto_eletronico.domain.dto.company.DeleteCompanyRequestDto
import br.com.santanna.ponto_eletronico.domain.dto.todto.CompanyToDto
import br.com.santanna.ponto_eletronico.domain.entity.company.Address
import br.com.santanna.ponto_eletronico.domain.entity.company.Company
import br.com.santanna.ponto_eletronico.domain.entity.employee.Employee
import br.com.santanna.ponto_eletronico.domain.entity.employee.EmployeeRole
import br.com.santanna.ponto_eletronico.domain.service.CompanyService
import br.com.santanna.ponto_eletronico.infrastructure.config.CepService
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class CompanyServiceImpl(
    private val companyDataProvider: CompanyDataprovider,
    private val employeeDataProvider: EmployeeDataProvider,
    private val companyToDto: CompanyToDto,
    private val cepService: CepService
) : CompanyService {

    override fun getAllCompanies(pageable: Pageable): Page<CompanyWithEmployeeCountDto> {
        val companies = companyDataProvider.findAll(pageable)
        return companies.map { companyToDto.convertToCompanyWithEmployeeCountDto(it) }
    }

    override fun getCompanyByCNPJ(companyCNPJ: String?): CompanyDTO {
        val company = companyDataProvider.findByCompanyCNPJ(companyCNPJ)
        return companyToDto.convertToDtoCompany(company)
    }

    override fun getCompaniesByName(nameCompany: String): CompanyDTO {
        val company = companyDataProvider.findByNameCompanyContainsIgnoreCase(nameCompany)
        return companyToDto.convertToDtoCompany(company)
    }

    @Transactional
    override fun registerCompany(createCompanyDto: CreateCompanyDto): CompanyDTO {
        val isExistCompany = companyDataProvider.existsByNameCompanyIgnoreCase(createCompanyDto.nameCompany)
        if (isExistCompany) {
            throw DataIntegrityViolationException("Empresa já cadastrada")
        }
        val addressDto = createCompanyDto.address?.postalCode?.let { cepService.getEnderecoByCep(it) }

        if (addressDto == null) {
            throw IllegalArgumentException("CEP inválido ou não encontrado")
        }

        val address = Address(
            postalCode = addressDto.postalCode,
            street = addressDto.street,
            city = addressDto.city,
            state = addressDto.state,
            number = createCompanyDto.address?.number // Manter o número do DTO original
        )

        val companyEntity = Company(
            nameCompany = createCompanyDto.nameCompany,
            companyCNPJ = createCompanyDto.companyCNPJ,
            address = address
        )

        val savedCompanyEntity = companyDataProvider.save(companyEntity)

        val encryptedPassword = BCryptPasswordEncoder().encode(createCompanyDto.managerPasswords)

        val managerEntity = Employee(
            name = createCompanyDto.managerName,
            surname = createCompanyDto.managerSurname,
            position = createCompanyDto.managerPosition,
            cpf = createCompanyDto.managerCpf,
            email = createCompanyDto.managerEmail,
            role = EmployeeRole.MANAGER,
            passwords = encryptedPassword,
            company = savedCompanyEntity,

        )

        employeeDataProvider.save(managerEntity)

        return companyToDto.convertToDto(savedCompanyEntity)
}

@Transactional
override fun updateCompany(companyCNPJ: String?, companyDto: CompanyDTO): CompanyDTO {
    val existingCompany = companyDataProvider.findByCompanyCNPJ(companyCNPJ)

    existingCompany?.nameCompany = companyDto.nameCompany ?: existingCompany?.nameCompany
    existingCompany?.nameCompany = companyDto.nameCompany ?: existingCompany?.nameCompany
    val updatedAddress = companyDto.address?.postalCode?.let { postalCode ->
        val addressDto = cepService.getEnderecoByCep(postalCode)


        Address(
            street = addressDto.street,
            city = addressDto.city,
            state = addressDto.state,
            postalCode = postalCode,
            number = companyDto.address?.number
        )

    } ?: existingCompany?.address

    existingCompany?.address = updatedAddress

    val updatedCompanyEntity = existingCompany?.let { companyDataProvider.save(it) }
    return companyToDto.convertToDto(updatedCompanyEntity)
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


}
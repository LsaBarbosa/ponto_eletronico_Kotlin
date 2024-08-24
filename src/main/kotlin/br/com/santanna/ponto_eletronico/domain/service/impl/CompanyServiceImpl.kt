package br.com.santanna.ponto_eletronico.domain.service.impl

import br.com.santanna.ponto_eletronico.app.handler.model.BadRequestException
import br.com.santanna.ponto_eletronico.app.handler.model.NotFoundException
import br.com.santanna.ponto_eletronico.domain.dataprovider.CompanyDataprovider
import br.com.santanna.ponto_eletronico.domain.dataprovider.EmployeeDataProvider
import br.com.santanna.ponto_eletronico.domain.dto.company.*
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

    override fun getCompanyByCNPJ(companyCNPJ: String): CompanyDTO {
        val company = companyDataProvider.findByCompanyCNPJ(companyCNPJ)
            ?: throw NotFoundException("Empresa não encontrada: $companyCNPJ")
        return companyToDto.convertToDto(company)
    }

    override fun getCompaniesByName(nameCompany: String): CompanyDTO {
        val company = companyDataProvider.findByNameCompanyContainsIgnoreCase(nameCompany)
            ?: throw NotFoundException("Empresa não encontrada: $nameCompany")
        return companyToDto.convertToDto(company)
    }

    @Transactional
    override fun registerCompany(createCompanyDto: CreateCompanyDto): CompanyDTO {
        val isExistCompany = companyDataProvider.existsByNameCompanyIgnoreCase(createCompanyDto.nameCompany)
        if (isExistCompany) {
            throw BadRequestException("Empresa já cadastrada no sistema")
        }

        val addressDto = cepService.getEnderecoByCep(createCompanyDto.address.postalCode)

        val address = Address(
            postalCode = addressDto.postalCode,
            street = addressDto.street,
            city = addressDto.city,
            state = addressDto.state,
            number = createCompanyDto.address.number
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
            role = EmployeeRole.ADMIN,
            passwords = encryptedPassword,
            company = savedCompanyEntity,
        )

        employeeDataProvider.save(managerEntity)

        return companyToDto.convertToDto(savedCompanyEntity)
    }

    @Transactional
    override fun updateCompany(companyCNPJ: String, companyDto: CompanyDTO): CompanyDTO {
        val company = companyDataProvider.findByCompanyCNPJ(companyCNPJ)
            ?: throw NotFoundException("Empresa não encontrada para o CNPJ: $companyCNPJ")

        // Atualiza apenas os campos que foram fornecidos no DTO
        companyDto.nameCompany?.let { company.nameCompany = it }

        // Atualiza o endereço, se o DTO de endereço for fornecido
        companyDto.address?.let { dtoAddress ->
            // Se o postalCode foi fornecido, consulta o endereço atualizado via CepService
            val updatedAddress = cepService.getEnderecoByCep(dtoAddress.postalCode)

            // Atualiza os campos de endereço que foram fornecidos
            company.address = company.address.apply {
                street = dtoAddress.street ?: updatedAddress.street
                city = dtoAddress.city ?: updatedAddress.city
                state = dtoAddress.state ?: updatedAddress.state
                number = dtoAddress.number ?: this.number // Mantém o número atual se não for atualizado
                postalCode = updatedAddress.postalCode
            }
        }

        // Salva a empresa atualizada no banco de dados
        val updatedCompany = companyDataProvider.save(company)

        // Converte a entidade atualizada para DTO e retorna
        return companyToDto.convertToDto(updatedCompany)
    }
    @Transactional
    override fun deleteCompanyByCNPJ(deleteCompanyRequestDto: DeleteCompanyRequestDto) {
        companyDataProvider.findByCompanyCNPJ(deleteCompanyRequestDto.companyCNPJ)
            ?: throw NotFoundException("Empresa não encontrada: $deleteCompanyRequestDto.companyCNPJ")

        val employee = employeeDataProvider.findCpf(deleteCompanyRequestDto.employeeCpf)
            ?: throw NotFoundException("Employee not found with CPF: ${deleteCompanyRequestDto.employeeCpf}")

        val encryptedPassword = BCryptPasswordEncoder().matches(deleteCompanyRequestDto.passwords, employee.password)
        if (!encryptedPassword) {
            throw IllegalArgumentException("Invalid password")
        }

        companyDataProvider.deleteByCompanyCNPJ(deleteCompanyRequestDto.companyCNPJ)
    }


}
package br.com.santanna.ponto_eletronico.domain.service.impl

import br.com.santanna.ponto_eletronico.app.handler.model.DataIntegrityViolationException
import br.com.santanna.ponto_eletronico.domain.dataprovider.EmployeeDataProvider
import br.com.santanna.ponto_eletronico.domain.dto.company.CompanyGetDto
import br.com.santanna.ponto_eletronico.domain.dto.employee.EmployeeDto
import br.com.santanna.ponto_eletronico.domain.dto.employee.EmployeeGetDto
import br.com.santanna.ponto_eletronico.domain.dto.employee.UpdateEmployeeDto
import br.com.santanna.ponto_eletronico.domain.dto.timeRecord.TimeRecordDto
import br.com.santanna.ponto_eletronico.domain.entity.Company
import br.com.santanna.ponto_eletronico.domain.entity.Employee
import br.com.santanna.ponto_eletronico.domain.entity.TimeRecord
import br.com.santanna.ponto_eletronico.domain.service.EmployeeService
import br.com.santanna.ponto_eletronico.infrastructure.repository.CompanyRepository
import br.com.santanna.ponto_eletronico.infrastructure.security.login.Auth.Companion.EMPLOYEE_ALREADY_EXIST
import jakarta.validation.Valid
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service


@Service
class EmployeeServiceImpl(
    private val employeeDataProvider: EmployeeDataProvider,
    private val companyRepository: CompanyRepository,

) : EmployeeService {

    override fun getAllEmployees(): List<EmployeeGetDto> {
        val employees = employeeDataProvider.findAll()
        return employees.map { convertToGetEmployeeDto(it) }
    }

    override fun getEmployeeById(id: Long): EmployeeGetDto? {
        val employee = employeeDataProvider.findById(id)
        return convertToGetEmployeeDto(employee)
    }

    override fun getEmployeeByCpf(cpf: String): EmployeeGetDto? {
        val employee = employeeDataProvider.findCpf(cpf)
        return convertToGetEmployeeDto(employee)
    }


    override fun registerEmployee(@Valid employeeDto: EmployeeDto?): EmployeeDto {

        val employeeCpf = employeeDto?.cpf?.let { employeeDataProvider.findByCpf(it) }
        if (employeeCpf != null) {
            throw DataIntegrityViolationException(EMPLOYEE_ALREADY_EXIST)
        }
        val encryptedPassword = BCryptPasswordEncoder().encode(employeeDto?.passwords)

        val company = companyRepository.findByNameCompanyContainsIgnoreCase(employeeDto?.companyName)

        val employeeEntity = Employee(
            name = employeeDto?.name,
            surname = employeeDto?.surname,
            salary = employeeDto?.salary,
            position = employeeDto?.position,
            cpf = employeeDto?.cpf,
            role = employeeDto?.role,
            passwords = encryptedPassword,
            company = company
        )
        val savedEmployeeEntity = employeeDataProvider.save(employeeEntity)
        return convertToDto(savedEmployeeEntity)
    }

    override fun updateEmployee(cpf: String, @Valid updateEmployeeDto: UpdateEmployeeDto): UpdateEmployeeDto {
        val existingEmployeeEntity = employeeDataProvider.findCpf(cpf)
            ?: throw IllegalArgumentException("Employee not found with CPF: $cpf")

        val encryptedPassword = updateEmployeeDto.passwords?.let { BCryptPasswordEncoder().encode(it) }

        existingEmployeeEntity.apply {
            salary = updateEmployeeDto.salary ?: salary
            passwords = encryptedPassword ?: passwords
            position = updateEmployeeDto.position ?: position
            role = updateEmployeeDto.role ?: role
        }

        val updatedEmployeeEntity = employeeDataProvider.save(existingEmployeeEntity)
        return convertToUpdateEmployeeDto(updatedEmployeeEntity)
    }


    override fun deleteEmployee(cpf: String) {
        employeeDataProvider.deleteByCpf(cpf)
    }

   private fun convertToDto(employee: Employee?): EmployeeDto {
        return EmployeeDto(
            id = employee?.id,
            cpf = employee?.cpf,
            role = employee?.role,
            name = employee?.name,
            surname = employee?.surname,
            position = employee?.position,
            salary = employee?.salary,
            companyName = employee?.company?.nameCompany
        )
    }

    private fun convertToUpdateEmployeeDto(employee: Employee?): UpdateEmployeeDto {
        return UpdateEmployeeDto(
            cpf = employee?.cpf,
            role = employee?.role,
            position = employee?.position,
            salary = employee?.salary
        )
    }
}

    fun convertToGetEmployeeDto(employee: Employee?): EmployeeGetDto {
        val timeWorkedDtos = employee?.timeWorked?.map { convertToTimeRecordDto(it!!) }

        return EmployeeGetDto(
            id = employee?.id,
            name = employee?.name,
            surname = employee?.surname,
            salary = employee?.salary,
            position = employee?.position,
            password = employee?.passwords,
            timeWorked = timeWorkedDtos,
            company = employee?.company?.convertToDto()
        )
    }

    fun convertToTimeRecordDto(timeRecord: TimeRecord): TimeRecordDto {
        return TimeRecordDto(
            id = timeRecord.id,
            startWorkTime = timeRecord.startWorkTime,
            endWorkTime = timeRecord.endWorkTime,
            timeWorked = timeRecord.timeWorked
        )
    }

    fun Company.convertToDto(): CompanyGetDto {
        return CompanyGetDto(
            nameCompany = this.nameCompany
        )
    }



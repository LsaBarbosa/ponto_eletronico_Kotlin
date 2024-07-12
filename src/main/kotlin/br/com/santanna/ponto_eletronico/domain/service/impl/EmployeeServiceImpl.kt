package br.com.santanna.ponto_eletronico.domain.service.impl

import br.com.santanna.ponto_eletronico.app.handler.model.DataIntegrityViolationException
import br.com.santanna.ponto_eletronico.domain.dataprovider.EmployeeDataProvider
import br.com.santanna.ponto_eletronico.domain.dto.company.CompanyGetDto
import br.com.santanna.ponto_eletronico.domain.dto.employee.*
import br.com.santanna.ponto_eletronico.domain.dto.timeRecord.TimeRecordDto
import br.com.santanna.ponto_eletronico.domain.entity.Company
import br.com.santanna.ponto_eletronico.domain.entity.Employee
import br.com.santanna.ponto_eletronico.domain.entity.EmployeeRole
import br.com.santanna.ponto_eletronico.domain.entity.TimeRecord
import br.com.santanna.ponto_eletronico.domain.service.EmployeeService
import br.com.santanna.ponto_eletronico.infrastructure.repository.CompanyRepository
import br.com.santanna.ponto_eletronico.infrastructure.security.login.Auth.Companion.EMPLOYEE_ALREADY_EXIST
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service


@Service
class EmployeeServiceImpl(
    private val employeeDataProvider: EmployeeDataProvider,
    private val companyRepository: CompanyRepository

) : EmployeeService {

    override fun getAllEmployees(pageable: Pageable): Page<EmployeeGetDto> {
        val employees = employeeDataProvider.findAll(pageable)
        return employees.map { convertToGetEmployeeDto(it) }
    }

    override fun getEmployeeById(id: Long): EmployeeGetDto? {
        val employee = employeeDataProvider.findById(id)
        return convertToGetEmployeeDto(employee)
    }


    override fun getEmployeeByNameAndSurname(name: String, surname: String): EmployeeGetDto? {
        val employee = employeeDataProvider.findByNameAndSurnameIgnoreCase(name, surname)
        return convertToGetEmployeeDto(employee)
    }

    override fun getEmployeeEntityByCpf(cpf: String): Employee? {
        return employeeDataProvider.findCpf(cpf)
    }

    override fun getEmployeeByCpf(cpf: String): EmployeeGetDto? {
        val employee = employeeDataProvider.findCpf(cpf)
        return convertToGetEmployeeDto(employee)
    }


    @Transactional
    override fun registerEmployee(managerCpf: String, createEmployeeDto: CreateEmployeeDto): EmployeeDto {
        val manager = employeeDataProvider.findCpf(createEmployeeDto.managerCpf)
            ?: throw IllegalArgumentException("Manager not found with CPF: ${createEmployeeDto.managerCpf}")

        if (manager.role != EmployeeRole.MANAGER) {
            throw IllegalArgumentException("The specified manager is not authorized to create employees.")
        }

        val isPasswordValid = BCryptPasswordEncoder().matches(createEmployeeDto.passwordsManager, manager.password)
        if (!isPasswordValid) {
            throw IllegalArgumentException("Invalid password")
        }

        val employeeCpf = createEmployeeDto.cpf.let { employeeDataProvider.findCpf(it) }
        if (employeeCpf != null) {
            throw DataIntegrityViolationException(EMPLOYEE_ALREADY_EXIST)
        }

        val encryptedPassword = BCryptPasswordEncoder().encode(createEmployeeDto.passwords)
        val company = manager.company
            ?: throw IllegalArgumentException("Manager does not belong to any company.")

        val employeeEntity = Employee(
            name = createEmployeeDto.name,
            surname = createEmployeeDto.surname,
            salary = createEmployeeDto.salary,
            position = createEmployeeDto.position,
            cpf = createEmployeeDto.cpf,
            role = createEmployeeDto.role ?: EmployeeRole.USER,
            passwords = encryptedPassword,
            company = company
        )

        val savedEmployeeEntity = employeeDataProvider.save(employeeEntity)
        return convertToDto(savedEmployeeEntity)
    }

    @Transactional
    override fun updateEmployee(updateEmployeeRequestDto: UpdateEmployeeRequestDto): UpdateEmployeeDto {
        val employeeToUpdate = employeeDataProvider.findCpf(updateEmployeeRequestDto.employeeCpfTarget)
            ?: throw IllegalArgumentException("Employee not found with CPF: ${updateEmployeeRequestDto.employeeCpfTarget}")

        val updatingEmployee = employeeDataProvider.findCpf(updateEmployeeRequestDto.employeeManagerCpf)
            ?: throw IllegalArgumentException("Employee not found with CPF: ${updateEmployeeRequestDto.employeeManagerCpf}")

        val isPasswordValid = BCryptPasswordEncoder().matches(updateEmployeeRequestDto.passwords, updatingEmployee.password)
        if (!isPasswordValid) {
            throw IllegalArgumentException("Invalid password")
        }

        val encryptedPassword = updateEmployeeRequestDto.updateEmployeeDto.passwords?.let { BCryptPasswordEncoder().encode(it) }

        employeeToUpdate.apply {
            salary = updateEmployeeRequestDto.updateEmployeeDto.salary ?: salary
            passwords = encryptedPassword ?: passwords
            position = updateEmployeeRequestDto.updateEmployeeDto.position ?: position
            role = updateEmployeeRequestDto.updateEmployeeDto.role ?: role
        }

        val updatedEmployeeEntity = employeeDataProvider.save(employeeToUpdate)
        return convertToUpdateEmployeeDto(updatedEmployeeEntity)
    }

    @Transactional
    override  fun deleteEmployee(deleteEmployeeRequestDto: DeleteEmployeeRequestDto) {
        val employeeTarget = employeeDataProvider.findCpf(deleteEmployeeRequestDto.employeeCpfTarget)
            ?: throw IllegalArgumentException("Employee not found with CPF: ${deleteEmployeeRequestDto.employeeCpfTarget}")

        val deletingEmployee = employeeDataProvider.findCpf(deleteEmployeeRequestDto.employeeManagerCpf)
            ?: throw IllegalArgumentException("Employee not found with CPF: ${deleteEmployeeRequestDto.employeeManagerCpf}")

        val isPasswordValid = BCryptPasswordEncoder().matches(deleteEmployeeRequestDto.passwords, deletingEmployee.password)
        if (!isPasswordValid) {
            throw IllegalArgumentException("Invalid password")
        }

        employeeDataProvider.deleteByCpf(deleteEmployeeRequestDto.employeeCpfTarget)
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
            companyCNPJ = employee?.company?.companyCNPJ
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

private fun convertToGetEmployeeDto(employee: Employee?): EmployeeGetDto {
    employee?.timeWorked?.map { convertToTimeRecordDto(it!!) }

    return EmployeeGetDto(
        id = employee?.id,
        name = employee?.name,
        surname = employee?.surname,
        salary = employee?.salary,
        position = employee?.position,
        cpf = employee?.cpf,
        role = employee?.role,
        company = employee?.company?.convertToDto()
    )
}

private fun convertToTimeRecordDto(timeRecord: TimeRecord): TimeRecordDto {
    return TimeRecordDto(
        id = timeRecord.id,
        startWorkTime = timeRecord.startWorkTime,
        endWorkTime = timeRecord.endWorkTime,
        timeWorked = timeRecord.timeWorked
    )
}

private fun Company.convertToDto(): CompanyGetDto {
    return CompanyGetDto(
        nameCompany = this.nameCompany
    )
}



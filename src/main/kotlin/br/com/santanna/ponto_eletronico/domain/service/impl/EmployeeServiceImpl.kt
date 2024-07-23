package br.com.santanna.ponto_eletronico.domain.service.impl

import br.com.santanna.ponto_eletronico.app.handler.model.DataIntegrityViolationException
import br.com.santanna.ponto_eletronico.app.handler.model.ObjectNotFoundException
import br.com.santanna.ponto_eletronico.domain.dataprovider.EmployeeDataProvider
import br.com.santanna.ponto_eletronico.domain.dto.employee.*
import br.com.santanna.ponto_eletronico.domain.dto.todto.EmployeeToDto
import br.com.santanna.ponto_eletronico.domain.entity.employee.Employee
import br.com.santanna.ponto_eletronico.domain.entity.employee.EmployeeRole
import br.com.santanna.ponto_eletronico.domain.service.EmployeeService
import br.com.santanna.ponto_eletronico.domain.service.util.employee.EmployeeServiceUtils
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service


@Service
class EmployeeServiceImpl(
    private val employeeDataProvider: EmployeeDataProvider,
    private val employeeServiceUtils: EmployeeServiceUtils,
    private val employeeToDto: EmployeeToDto
) : EmployeeService {

    override fun getEmployeeById(): EmployeeGetDto? {
        val id = employeeServiceUtils.getCurrentUserId()
        val employee = employeeDataProvider.findById(id)
        return employeeToDto.convertToGetEmployeeDto(employee)
    }

    override fun getEmployeeEntityByCpf(cpf: String): Employee? {
        return employeeDataProvider.findCpf(cpf)
    }

    @Transactional
    override fun updatePassword(updatePasswordDto: UpdatePassword) {
        val id = employeeServiceUtils.getCurrentUserId()
        val employee = employeeDataProvider.findById(id)

        val isOldPasswordValid = BCryptPasswordEncoder().matches(updatePasswordDto.oldPassword, employee.password)
        if (!isOldPasswordValid) {
            throw IllegalArgumentException("Invalid old password")
        }

        if (updatePasswordDto.newPassword != updatePasswordDto.confirmPassword) {
            throw IllegalArgumentException("New password and confirmation do not match")
        }

        val newEncryptedPassword = BCryptPasswordEncoder().encode(updatePasswordDto.newPassword)
        employee.passwords = newEncryptedPassword

        employeeDataProvider.save(employee)
    }

    @Transactional
    override fun resetPassword(resetPasswordDto: ResetPasswordDto) {
        val employee = employeeDataProvider.findCpf(resetPasswordDto.cpf)
            ?: throw ObjectNotFoundException("Employee not found with CPF: ${resetPasswordDto.cpf}")

        if (employee.email != resetPasswordDto.email) {
            throw IllegalArgumentException("Invalid email")
        }

        val newPassword = employeeServiceUtils.generateRandomPassword()
        val newEncryptedPassword = BCryptPasswordEncoder().encode(newPassword)
        employee.passwords = newEncryptedPassword
        employeeDataProvider.save(employee)

        employeeServiceUtils.sendEmail(employee.email!!, newPassword)
    }

    @Transactional
    override fun registerEmployeeAsManager(createEmployeeDto: CreateEmployeeDto): EmployeeDto {
        val manager = employeeServiceUtils.validateManager(createEmployeeDto.passwordsManager)

        val employeeCpf = createEmployeeDto.cpf.let { employeeDataProvider.findCpf(it) }
        if (employeeCpf != null) {
            throw DataIntegrityViolationException("EMPLOYEE_ALREADY_EXIST")
        }

        val encryptedPassword = BCryptPasswordEncoder().encode(createEmployeeDto.passwords)
        val employeeEntity = Employee(
            name = createEmployeeDto.name,
            surname = createEmployeeDto.surname,
            salary = createEmployeeDto.salary,
            position = createEmployeeDto.position,
            cpf = createEmployeeDto.cpf,
            email = createEmployeeDto.email,
            role = createEmployeeDto.role ?: EmployeeRole.USER,
            passwords = encryptedPassword,
            company = manager.company
        )

        val savedEmployeeEntity = employeeDataProvider.save(employeeEntity)
        return employeeToDto.convertToDto(savedEmployeeEntity)
    }

    @Transactional
    override fun updateEmployeeAsManager(updateEmployeeRequestDto: UpdateEmployeeRequestDto): UpdateEmployeeDto {
        val employeeToUpdate = employeeDataProvider.findCpf(updateEmployeeRequestDto.employeeCpfTarget)
            ?: throw IllegalArgumentException("Employee not found with CPF: ${updateEmployeeRequestDto.employeeCpfTarget}")

        employeeServiceUtils.validateManager(updateEmployeeRequestDto.passwords)

        updateEmployeeRequestDto.updateEmployeeDto.apply {
            name?.let { employeeToUpdate.name = it }
            surname?.let { employeeToUpdate.surname = it }
            salary?.let { employeeToUpdate.salary = it }
            email?.let { employeeToUpdate.email = it }
            position?.let { employeeToUpdate.position = it }
            role?.let { employeeToUpdate.role = it }
        }

        val updatedEmployeeEntity = employeeDataProvider.save(employeeToUpdate)
        return employeeToDto.convertToUpdateEmployeeDto(updatedEmployeeEntity)
    }

    @Transactional
    override fun deleteEmployeeAsManager(deleteEmployeeRequestDto: DeleteEmployeeRequestDto) {

         employeeDataProvider.findCpf(deleteEmployeeRequestDto.employeeCpfTarget)
            ?: throw IllegalArgumentException("Employee not found with CPF: ${deleteEmployeeRequestDto.employeeCpfTarget}")

        employeeServiceUtils.validateManager(deleteEmployeeRequestDto.passwords)

        employeeDataProvider.deleteByCpf(deleteEmployeeRequestDto.employeeCpfTarget)
    }

    override fun getEmployeesAsManager(managerEmployeeRequestDto: ManagerEmployeeRequestDto, pageable: Pageable): Page<EmployeeGetDto> {
        val id = employeeServiceUtils.getCurrentUserId()
        val manager = employeeDataProvider.findById(id)

        if (manager.role != EmployeeRole.MANAGER) {
            throw IllegalArgumentException("The specified employee is not a manager.")
        }

        val company = manager.company
            ?: throw IllegalArgumentException("Manager does not belong to any company.")

        val employees = employeeDataProvider.findByCompany(company.id!!, pageable)
        return employees.map { employeeToDto.convertToGetEmployeeDto(it) }
    }

    override fun getEmployeeByNameAndSurnameAsManager(name: String, surname: String): EmployeeGetDto? {
        val id = employeeServiceUtils.getCurrentUserId()
        val manager = employeeDataProvider.findById(id)

        if (manager.role != EmployeeRole.MANAGER) {
            throw IllegalArgumentException("The specified employee is not a manager.")
        }

        val company = manager.company
            ?: throw IllegalArgumentException("Manager does not belong to any company.")

        val employee = employeeDataProvider.findByNameAndSurnameIgnoreCase(name, surname)
        return employeeToDto.convertToGetEmployeeDto(employee)
    }

    override fun getEmployeeByCpfAsManager(request: ManagerEmployeeRequestByCPFDto): EmployeeGetDto? {
        val manager = employeeServiceUtils.validateManager(request.passwords)

        val employee = employeeDataProvider.findCpf(request.employeeCpf)
            ?: throw IllegalArgumentException("Employee not found with CPF: ${request.employeeCpf}")

        if (employee.company?.id != manager.company!!.id) {
            throw IllegalArgumentException("The specified employee does not belong to the manager's company.")
        }

        return employeeToDto.convertToGetEmployeeDto(employee)
    }

}

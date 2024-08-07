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
import java.util.*


private const val EMPLOYEE_NOT_FOUND = "Colaborador com CPF:"

private const val IS_NOT_MANAGER = "Colaborador não possui permissão para esse recurso."

private const val EMPLOYEE_DIFERENT_COMPANY = "Colaborador nao está na mesma empresa que o gerente."

private const val NOT_FOUND = "não encontrado"

private const val MANAGER_WITHOUT_COMPANY = "Gerente não cadastrado em nenhuma empresa."



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
            throw IllegalArgumentException("Senha antiga inválida")
        }

        if (updatePasswordDto.newPassword != updatePasswordDto.confirmPassword) {
            throw IllegalArgumentException("Nova senha e confirmação de senha não correspondem")
        }

        val newEncryptedPassword = BCryptPasswordEncoder().encode(updatePasswordDto.newPassword)
        employee.passwords = newEncryptedPassword

        employeeDataProvider.save(employee)
    }

    @Transactional
    override fun resetPassword(resetPasswordDto: ResetPasswordDto) {
        val employee = employeeDataProvider.findCpf(resetPasswordDto.cpf)
            ?: throw ObjectNotFoundException("$EMPLOYEE_NOT_FOUND ${resetPasswordDto.cpf} $NOT_FOUND")

        if (employee.email != resetPasswordDto.email) {
            throw IllegalArgumentException("Email inválido")
        }

        val newPassword = employeeServiceUtils.generateRandomPassword()
        val newEncryptedPassword = BCryptPasswordEncoder().encode(newPassword)
        employee.passwords = newEncryptedPassword
        employeeDataProvider.save(employee)

        employeeServiceUtils.sendEmail(employee.email, newPassword)
    }

    @Transactional
    override fun registerEmployeeAsManager(createEmployeeDto: CreateEmployeeDto): EmployeeDto {
        val manager = employeeServiceUtils.validateManager(createEmployeeDto.passwordsManager)

        val employeeCpf = createEmployeeDto.cpf.let { employeeDataProvider.findCpf(it) }
        if (employeeCpf != null) {
            throw DataIntegrityViolationException("Colaborador já existe no sistema")
        }

        val encryptedPassword = BCryptPasswordEncoder().encode(createEmployeeDto.passwords)
        val employeeEntity = Employee(
            name = createEmployeeDto.name,
            surname = createEmployeeDto.surname,
            salary = createEmployeeDto.salary,
            position = createEmployeeDto.position,
            cpf = createEmployeeDto.cpf,
            email = createEmployeeDto.email,
            role = createEmployeeDto.role,
            passwords = encryptedPassword,
            company = manager.company
        )

        val savedEmployeeEntity = employeeDataProvider.save(employeeEntity)
        return employeeToDto.convertToDto(savedEmployeeEntity)
    }

    @Transactional
    override fun updateEmployeeAsManager(updateEmployeeRequestDto: UpdateEmployeeRequestDto): UpdateEmployeeDto {
        val id = employeeServiceUtils.getCurrentUserId()
        val manager = employeeDataProvider.findById(id)
        employeeServiceUtils.validateSameCompany(updateEmployeeRequestDto.employeeId, manager)

        val employeeToUpdate = employeeDataProvider.findById(updateEmployeeRequestDto.employeeId)


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
        val manager = employeeServiceUtils.validateManager(deleteEmployeeRequestDto.passwords)
        employeeServiceUtils.validateSameCompany(deleteEmployeeRequestDto.employeeId, manager)

        employeeDataProvider.findById(deleteEmployeeRequestDto.employeeId)
        employeeDataProvider.deleteById(deleteEmployeeRequestDto.employeeId)
    }

    override fun getEmployeesAsManager(pageable: Pageable): Page<EmployeeGetDto> {
        val id = employeeServiceUtils.getCurrentUserId()
        val manager = employeeDataProvider.findById(id)

        if (manager.role != EmployeeRole.MANAGER) {
            throw IllegalArgumentException(IS_NOT_MANAGER)
        }

        val company = manager.company
            ?: throw IllegalArgumentException(MANAGER_WITHOUT_COMPANY)

        val employees = employeeDataProvider.findByCompany(company.id!!, pageable)
        return employees.map { employeeToDto.convertToGetEmployeeDto(it) }
    }

    @Transactional
    override fun getEmployeeByIdAsManager(id: UUID): EmployeeGetDto {
        val managerId = employeeServiceUtils.getCurrentUserId()
        val manager = employeeDataProvider.findById(managerId)

        if (manager.role != EmployeeRole.MANAGER) {
            throw IllegalArgumentException("The specified employee is not a manager.")
        }

        val employee = employeeDataProvider.findById(id)

        if (employee.company?.id != manager.company?.id) {
            throw IllegalArgumentException("The specified employee does not belong to the manager's company.")
        }

        return employeeToDto.convertToGetEmployeeDto(employee)
    }

    override fun getEmployeeByNameAndSurnameAsManager(name: String, surname: String): EmployeeGetDto? {
        val id = employeeServiceUtils.getCurrentUserId()
        val manager = employeeDataProvider.findById(id)

        if (manager.role != EmployeeRole.MANAGER) {
            throw IllegalArgumentException(IS_NOT_MANAGER)
        }

        manager.company
            ?: throw IllegalArgumentException(MANAGER_WITHOUT_COMPANY)

        val employee = employeeDataProvider.findByNameAndSurnameIgnoreCase(name, surname)
        return employeeToDto.convertToGetEmployeeDto(employee)
    }

    override fun getEmployeeByCpfAsManager(request: ManagerEmployeeRequestByCPFDto): EmployeeGetDto? {
        val id = employeeServiceUtils.getCurrentUserId()
        val manager = employeeDataProvider.findById(id)
employeeServiceUtils.validateSameCompany(request.employeeCpf,manager)

        val employee = employeeDataProvider.findCpf(request.employeeCpf)
            ?: throw IllegalArgumentException("$EMPLOYEE_NOT_FOUND ${request.employeeCpf} $NOT_FOUND")

        if (employee.company?.id != manager.company!!.id) {
            throw IllegalArgumentException(EMPLOYEE_DIFERENT_COMPANY)
        }

        return employeeToDto.convertToGetEmployeeDto(employee)
    }

}

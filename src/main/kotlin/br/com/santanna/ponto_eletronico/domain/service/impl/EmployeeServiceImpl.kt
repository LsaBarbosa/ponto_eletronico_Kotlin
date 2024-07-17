package br.com.santanna.ponto_eletronico.domain.service.impl

import br.com.santanna.ponto_eletronico.app.handler.model.DataIntegrityViolationException
import br.com.santanna.ponto_eletronico.app.handler.model.ObjectNotFoundException
import br.com.santanna.ponto_eletronico.domain.dataprovider.EmployeeDataProvider
import br.com.santanna.ponto_eletronico.domain.dto.company.CompanyGetDto
import br.com.santanna.ponto_eletronico.domain.dto.employee.*
import br.com.santanna.ponto_eletronico.domain.dto.timeRecord.TimeRecordDto
import br.com.santanna.ponto_eletronico.domain.entity.Company
import br.com.santanna.ponto_eletronico.domain.entity.Employee
import br.com.santanna.ponto_eletronico.domain.entity.EmployeeRole
import br.com.santanna.ponto_eletronico.domain.entity.TimeRecord
import br.com.santanna.ponto_eletronico.domain.service.EmployeeService
import br.com.santanna.ponto_eletronico.infrastructure.security.configsec.JwtTokenUtil

import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.util.*


@Service
class EmployeeServiceImpl(
    private val employeeDataProvider: EmployeeDataProvider,
    private val mailSender: JavaMailSenderImpl, private val jwtTokenUtil: JwtTokenUtil) : EmployeeService {

    override fun getEmployeesByManager(managerEmployeeRequestDto: ManagerEmployeeRequestDto, pageable: Pageable): Page<EmployeeGetDto> {
        val id = getCurrentUserId()
        val manager = employeeDataProvider.findById(id)

        if (manager.role != EmployeeRole.MANAGER) {
            throw IllegalArgumentException("The specified employee is not a manager.")
        }

        val company = manager.company
            ?: throw IllegalArgumentException("Manager does not belong to any company.")

        val employees = employeeDataProvider.findByCompany(company.id!!, pageable)
        return employees.map { convertToGetEmployeeDto(it) }
    }

    override fun getEmployeeById(): EmployeeGetDto? {
        val id = getCurrentUserId()
        val employee = employeeDataProvider.findById(id)
        return convertToGetEmployeeDto(employee)
    }

    override fun getEmployeeByNameAndSurname(name: String, surname: String): EmployeeGetDto? {
        val id = getCurrentUserId()
        val manager = employeeDataProvider.findById(id)

        if (manager.role != EmployeeRole.MANAGER) {
            throw IllegalArgumentException("The specified employee is not a manager.")
        }

        val company = manager.company
            ?: throw IllegalArgumentException("Manager does not belong to any company.")

        val employee = employeeDataProvider.findByNameAndSurnameIgnoreCase(name, surname)
        return convertToGetEmployeeDto(employee)
    }

    override fun getEmployeeEntityByCpf(cpf: String): Employee? {
        return employeeDataProvider.findCpf(cpf)
    }

    override fun getEmployeeByCpf(request: ManagerEmployeeRequestByCPFDto): EmployeeGetDto? {
        val manager = validateManager(request.passwords)

        val employee = employeeDataProvider.findCpf(request.employeeCpf)
            ?: throw IllegalArgumentException("Employee not found with CPF: ${request.employeeCpf}")

        if (employee.company?.id != manager.company!!.id) {
            throw IllegalArgumentException("The specified employee does not belong to the manager's company.")
        }

        return convertToGetEmployeeDto(employee)
    }

    @Transactional
    override fun registerEmployee(createEmployeeDto: CreateEmployeeDto): EmployeeDto {
        val manager = validateManager(createEmployeeDto.passwordsManager)

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
        return convertToDto(savedEmployeeEntity)
    }

    @Transactional
    override fun updateEmployee(updateEmployeeRequestDto: UpdateEmployeeRequestDto): UpdateEmployeeDto {
        val employeeToUpdate = employeeDataProvider.findCpf(updateEmployeeRequestDto.employeeCpfTarget)
            ?: throw IllegalArgumentException("Employee not found with CPF: ${updateEmployeeRequestDto.employeeCpfTarget}")

        validateManager(updateEmployeeRequestDto.passwords)

        updateEmployeeRequestDto.updateEmployeeDto.apply {
            name?.let { employeeToUpdate.name = it }
            surname?.let { employeeToUpdate.surname = it }
            salary?.let { employeeToUpdate.salary = it }
            email?.let { employeeToUpdate.email = it }
            position?.let { employeeToUpdate.position = it }
            role?.let { employeeToUpdate.role = it }
        }

        val updatedEmployeeEntity = employeeDataProvider.save(employeeToUpdate)
        return convertToUpdateEmployeeDto(updatedEmployeeEntity)
    }

    @Transactional
    override fun updatePassword(updatePasswordDto: UpdatePassword) {
        val id = getCurrentUserId()
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
    override fun deleteEmployee(deleteEmployeeRequestDto: DeleteEmployeeRequestDto) {
         employeeDataProvider.findCpf(deleteEmployeeRequestDto.employeeCpfTarget)
            ?: throw IllegalArgumentException("Employee not found with CPF: ${deleteEmployeeRequestDto.employeeCpfTarget}")

        validateManager(deleteEmployeeRequestDto.passwords)

        employeeDataProvider.deleteByCpf(deleteEmployeeRequestDto.employeeCpfTarget)
    }

    @Transactional
    override fun resetPassword(resetPasswordDto: ResetPasswordDto) {
        val employee = employeeDataProvider.findCpf(resetPasswordDto.cpf)
            ?: throw ObjectNotFoundException("Employee not found with CPF: ${resetPasswordDto.cpf}")

        if (employee.email != resetPasswordDto.email) {
            throw IllegalArgumentException("Invalid email")
        }

        val newPassword = generateRandomPassword()
        val newEncryptedPassword = BCryptPasswordEncoder().encode(newPassword)
        employee.passwords = newEncryptedPassword
        employeeDataProvider.save(employee)

        sendEmail(employee.email!!, newPassword)
    }


    private fun validateManager(password: String): Employee {
        val id = getCurrentUserId()
        val manager = employeeDataProvider.findById(id)

        if (manager.role != EmployeeRole.MANAGER) {
            throw IllegalArgumentException("The specified employee is not a manager.")
        }

        val isPasswordValid = BCryptPasswordEncoder().matches(password, manager.password)
        if (!isPasswordValid) {
            throw IllegalArgumentException("Invalid password")
        }

        return manager
    }

    private fun generateRandomPassword(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val random = SecureRandom()
        val password = StringBuilder()
        for (i in 0 until 8) {
            password.append(chars[random.nextInt(chars.length)])
        }
        return password.toString()
    }

    private fun sendEmail(to: String, newPassword: String) {
        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true)

        helper.setTo(to)
        helper.setSubject("Redefinição de Senha usuário Kronos")
        helper.setText("Olá,espero que tudo esteja bem!\n\n\n Aqui está sua senha provisória $newPassword \n\n\n\n Você no controle do seu TEMPO")
        val htmlContent = """
            <html>
            <body>
                <h1>Redefinição de Senha</h1>
                <h2>Olá,espero que tudo esteja bem!</h2>
                <p>Sua nova senha é: <strong>$newPassword</strong></p>
                <p>Por favor, altere sua senha ao fazer o login.</p>
                <br/>
                <p>Atenciosamente,</p>
                <h3>Equipe Kronos, você no controle do seu TEMPO</h3>
            </body>
            </html>
        """.trimIndent()

        helper.setText(htmlContent, true)
        mailSender.send(message)
    }

    private fun getCurrentUserId(): UUID {
        val authentication = SecurityContextHolder.getContext().authentication as UsernamePasswordAuthenticationToken
        val token = authentication.credentials as String
        return jwtTokenUtil.getUserIdFromToken(token)
    }

    private fun convertToDto(employee: Employee?): EmployeeDto {
        return EmployeeDto(
            id = employee?.id,
            cpf = employee?.cpf,
            role = employee?.role,
            name = employee?.name,
            email = employee?.email,
            surname = employee?.surname,
            position = employee?.position,
            salary = employee?.salary,
            companyCNPJ = employee?.company?.companyCNPJ
        )
    }

    private fun convertToUpdateEmployeeDto(employee: Employee?): UpdateEmployeeDto {
        return UpdateEmployeeDto(
            name = employee?.name,
            surname = employee?.surname,
            salary = employee?.salary,
            email = employee?.email,
            position = employee?.position,
            role = employee?.role
        )
    }

    private fun convertToGetEmployeeDto(employee: Employee?): EmployeeGetDto {
        employee?.timeWorked?.map { convertToTimeRecordDto(it!!) }

        return EmployeeGetDto(
            name = employee?.name,
            surname = employee?.surname,
            salary = employee?.salary,
            position = employee?.position,
            cpf = employee?.cpf,
            role = employee?.role,
            email = employee?.email,
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

}

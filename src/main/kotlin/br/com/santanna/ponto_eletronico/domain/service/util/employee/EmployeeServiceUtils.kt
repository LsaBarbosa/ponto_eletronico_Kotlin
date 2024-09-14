package br.com.santanna.ponto_eletronico.domain.service.util.employee


import br.com.santanna.ponto_eletronico.app.handler.model.BadRequestException
import br.com.santanna.ponto_eletronico.app.handler.model.UnauthorizedException
import br.com.santanna.ponto_eletronico.domain.dataprovider.EmployeeDataProvider
import br.com.santanna.ponto_eletronico.domain.entity.employee.Employee
import br.com.santanna.ponto_eletronico.domain.entity.employee.EmployeeRole
import br.com.santanna.ponto_eletronico.infrastructure.security.jwt.JwtTokenUtil
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.*

private const val EMPLOYEE_UNAUTHORIZED = "Colaborador sem permissão para o recurso"

@Component
class EmployeeServiceUtils(
    private val employeeDataProvider: EmployeeDataProvider, private val jwtTokenUtil: JwtTokenUtil,
    private val mailSender: JavaMailSenderImpl,
) {

    fun getCurrentUserId(): UUID {
        val authentication = SecurityContextHolder.getContext().authentication as UsernamePasswordAuthenticationToken
        val token = authentication.credentials as String
        return jwtTokenUtil.getUserIdFromToken(token)
    }

    fun validateManager(password: String): Employee {
        val manager = validateManagerWithoutPassword()

        val isPasswordValid = BCryptPasswordEncoder().matches(password, manager.password)
        if (!isPasswordValid) {
            throw UnauthorizedException("Senha Inválida")
        }

        return manager
    }

    fun validateManagerWithoutPassword(): Employee {
        val id = getCurrentUserId()
        val manager = employeeDataProvider.findById(id)

        if (manager.role != EmployeeRole.ADMIN && manager.role != EmployeeRole.MANAGER) {
            throw UnauthorizedException(EMPLOYEE_UNAUTHORIZED)
        }

        return manager
    }

    fun validateSameCompany(employeeId: UUID, manager: Employee) {
        val employee = employeeDataProvider.findById(employeeId)
        if (employee.company?.id != manager.company?.id) {
            throw BadRequestException("Colaborador não está na mesma empresa que o gestor.")
        }
    }

    fun generateRandomPassword(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val random = SecureRandom()
        val password = StringBuilder()
        for (i in 0 until 8) {
            password.append(chars[random.nextInt(chars.length)])
        }
        return password.toString()
    }

    fun sendEmail(to: String, newPassword: String) {
        try {
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
        } catch (e: Exception) {
            throw BadRequestException("Falha ao enviar o e-mail de redefinição de senha: ${e.message}")
        }
    }
}
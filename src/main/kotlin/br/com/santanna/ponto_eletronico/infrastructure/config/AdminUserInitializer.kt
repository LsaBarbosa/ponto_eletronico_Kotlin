package br.com.santanna.ponto_eletronico.infrastructure.config

import br.com.santanna.ponto_eletronico.domain.dataprovider.EmployeeDataProvider
import br.com.santanna.ponto_eletronico.domain.entity.employee.Employee
import br.com.santanna.ponto_eletronico.domain.entity.employee.EmployeeRole
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class AdminUserInitializer(
    private val employeeDataProvider: EmployeeDataProvider,

) : ApplicationListener<ApplicationReadyEvent> {
    private lateinit var passwordEncoder: BCryptPasswordEncoder
    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        val adminCpf = "11366653742"  // CPF do admin
        val adminEmail = "kronos.time.tech.solutions@gmail.com"
        val adminPassword = "Bmsant12126969*"

        if (employeeDataProvider.findCpf(adminCpf) == null) {
            val admin = Employee(
                cpf = adminCpf,
                email = adminEmail,
                passwords = passwordEncoder.encode(adminPassword),
                role = EmployeeRole.ADMIN,
                name = "Lucas",
                surname = "SantAnna",
                position = "Administrator"
            )
            employeeDataProvider.save(admin)
            println("Admin user created: $adminEmail")
        } else {
            println("Admin user already exists: $adminEmail")
        }
    }
}
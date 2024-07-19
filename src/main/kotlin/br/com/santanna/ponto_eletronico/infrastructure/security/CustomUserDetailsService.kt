package br.com.santanna.ponto_eletronico.infrastructure.security

import br.com.santanna.ponto_eletronico.domain.dataprovider.EmployeeDataProvider
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService (
    private val employeeDataProvider: EmployeeDataProvider
) : UserDetailsService {
    override fun loadUserByUsername(cpf: String): UserDetails {
        val employee = employeeDataProvider.findCpf(cpf)
            ?: throw UsernameNotFoundException("User not found with CPF: $cpf")
        return org.springframework.security.core.userdetails.User(employee.cpf, employee.password, emptyList())
    }
}
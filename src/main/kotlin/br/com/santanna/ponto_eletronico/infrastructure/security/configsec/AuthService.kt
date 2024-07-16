package br.com.santanna.ponto_eletronico.infrastructure.security.configsec

import br.com.santanna.ponto_eletronico.domain.dataprovider.EmployeeDataProvider
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service

@Service
class AuthService (
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenUtil: JwtTokenUtil,
    private val employeeDataProvider: EmployeeDataProvider
) {
    fun authenticate(cpf: String, password: String): String {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(cpf, password)
        )
        val employee = employeeDataProvider.findCpf(cpf)
            ?: throw IllegalArgumentException("Employee not found with CPF: $cpf")

        return jwtTokenUtil.generateToken(cpf, employee.id!!)
    }
}
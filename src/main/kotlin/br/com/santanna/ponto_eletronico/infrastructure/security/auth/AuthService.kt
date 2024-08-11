package br.com.santanna.ponto_eletronico.infrastructure.security.auth

import br.com.santanna.ponto_eletronico.domain.dataprovider.EmployeeDataProvider
import br.com.santanna.ponto_eletronico.infrastructure.security.CustomUserDetailsService
import br.com.santanna.ponto_eletronico.infrastructure.security.jwt.JwtResponse
import br.com.santanna.ponto_eletronico.infrastructure.security.jwt.JwtTokenUtil
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class AuthService (
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenUtil: JwtTokenUtil,
    private val employeeDataProvider: EmployeeDataProvider,
    private val customUserDetailsService: CustomUserDetailsService
) {
    fun authenticate(authenticationRequest: AuthenticationRequest): ResponseEntity<JwtResponse> {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(authenticationRequest.cpf, authenticationRequest.password)
        )

        SecurityContextHolder.getContext().authentication = authentication

        val userDetails = customUserDetailsService.loadUserByUsername(authenticationRequest.cpf)
        val user = employeeDataProvider.findCpf(authenticationRequest.cpf)
            ?: throw IllegalArgumentException("Employee not found with CPF: ${authenticationRequest.cpf}")

        val token = user.role?.let { jwtTokenUtil.generateToken(userDetails, user.id!!, it.name) }

        return ResponseEntity.ok(token?.let { JwtResponse(it) })
    }
}
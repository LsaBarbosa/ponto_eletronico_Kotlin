package br.com.santanna.ponto_eletronico.infrastructure.security.login

import br.com.santanna.ponto_eletronico.app.handler.model.DataIntegrityViolationException
import br.com.santanna.ponto_eletronico.domain.entity.Employee
import br.com.santanna.ponto_eletronico.infrastructure.security.configsec.TokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service

@Service
class Auth {

    companion object {
        const val EMPLOYEE_ALREADY_EXIST = "Já existe um colaborador com este CPF."
        const val USER_PASSWORD_WRONG = "Usuário ou senha inválidos."

    }

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager
    @Autowired
    private lateinit var tokenService: TokenService



    fun login(authenticationDTO: AuthenticationDTO): LoginResponseDTO {
        return try {
            val usernamePassword = UsernamePasswordAuthenticationToken(authenticationDTO.cpf, authenticationDTO.passwords)
            val auth = authenticationManager.authenticate(usernamePassword)
            val token = tokenService.generateToken(auth.principal as Employee)
            LoginResponseDTO(token)
        } catch (e: Exception) {
            throw DataIntegrityViolationException(USER_PASSWORD_WRONG)
        }
    }


}
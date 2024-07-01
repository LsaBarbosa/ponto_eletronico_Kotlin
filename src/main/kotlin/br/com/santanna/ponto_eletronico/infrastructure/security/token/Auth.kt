package br.com.santanna.ponto_eletronico.infrastructure.security.token

import br.com.santanna.ponto_eletronico.app.handler.model.DataIntegrityViolationException
import br.com.santanna.ponto_eletronico.domain.entity.Employee
import br.com.santanna.ponto_eletronico.infrastructure.repository.EmployeeRepository
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service

@Service
class Auth {

    companion object {
        const val EMPLOYEE_ALREADY_EXIST = "Já existe um colaborador com este nome."
        const val USER_PASSWORD_WRONG = "Usuário ou senha inválidos."
        const val ACCESS_DENIED = "Usuário não autorizado para cadastro."
    }

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var repository: EmployeeRepository

    @Autowired
    private lateinit var tokenService: TokenService

    @Autowired
    private lateinit var modelMapper: ModelMapper

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
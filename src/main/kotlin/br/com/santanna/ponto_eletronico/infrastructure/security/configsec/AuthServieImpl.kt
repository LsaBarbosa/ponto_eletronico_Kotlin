package br.com.santanna.ponto_eletronico.infrastructure.security.configsec

import br.com.santanna.ponto_eletronico.infrastructure.repository.EmployeeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class AuthorizationServiceImpl : UserDetailsService {

    @Autowired
    private lateinit var repository: EmployeeRepository

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        return repository.findByCpf(username) ?: throw UsernameNotFoundException("Usuário não encontrado: $username")
    }
}
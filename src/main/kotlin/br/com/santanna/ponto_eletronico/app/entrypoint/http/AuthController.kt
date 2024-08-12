package br.com.santanna.ponto_eletronico.app.entrypoint.http

import br.com.santanna.ponto_eletronico.infrastructure.security.auth.AuthService
import br.com.santanna.ponto_eletronico.infrastructure.security.jwt.JwtResponse
import br.com.santanna.ponto_eletronico.infrastructure.security.auth.AuthenticationRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/authentication")
@Tag(name = "Authentication", description = "End-point para autenticação do usuário no sistema")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/login")
    @Operation(
        summary = "Gerar token ",
        description = "Usuário ao realizar login com sucesso, o sistema gera um token que será utilizado no header.\n O id no usuario está dentro do token "
    )
    fun login(@RequestBody authenticationRequest: AuthenticationRequest): ResponseEntity<JwtResponse> {
        return authService.authenticate(authenticationRequest)
    }
}
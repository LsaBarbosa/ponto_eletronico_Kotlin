package br.com.santanna.ponto_eletronico.app.entrypoint.http

import br.com.santanna.ponto_eletronico.infrastructure.security.AuthService
import br.com.santanna.ponto_eletronico.infrastructure.security.JwtResponse
import br.com.santanna.ponto_eletronico.infrastructure.security.AuthenticationRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "End-point para autenticação")
class AuthController (
    private val authService: AuthService
) {
    @PostMapping("/login")
    @Operation(summary = "Registrando login com sucesso")
    fun login(@RequestBody authenticationRequest: AuthenticationRequest): ResponseEntity<JwtResponse> {
        return authService.authenticate(authenticationRequest)
    }
}
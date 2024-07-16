package br.com.santanna.ponto_eletronico.app.entrypoint.http

import br.com.santanna.ponto_eletronico.infrastructure.security.configsec.AuthService
import br.com.santanna.ponto_eletronico.infrastructure.security.configsec.JwtResponse
import br.com.santanna.ponto_eletronico.infrastructure.security.configsec.AuthenticationRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController (
    private val authService: AuthService
) {
    @PostMapping("/login")
    fun login(@RequestBody authenticationRequest: AuthenticationRequest): ResponseEntity<JwtResponse> {
        return authService.authenticate(authenticationRequest)
    }
}
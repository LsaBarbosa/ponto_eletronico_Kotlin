package br.com.santanna.ponto_eletronico.app.entrypoint.http

import br.com.santanna.ponto_eletronico.infrastructure.security.configsec.AuthService
import br.com.santanna.ponto_eletronico.infrastructure.security.configsec.JwtResponse
import br.com.santanna.ponto_eletronico.infrastructure.security.configsec.LoginRequest
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
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<JwtResponse> {
        val token = authService.authenticate(loginRequest.cpf, loginRequest.password)
        return ResponseEntity.ok(JwtResponse(token))
    }
}
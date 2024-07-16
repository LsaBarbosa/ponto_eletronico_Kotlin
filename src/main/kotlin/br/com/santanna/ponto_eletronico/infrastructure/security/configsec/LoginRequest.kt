package br.com.santanna.ponto_eletronico.infrastructure.security.configsec

data class LoginRequest( val cpf: String,
                         val password: String)

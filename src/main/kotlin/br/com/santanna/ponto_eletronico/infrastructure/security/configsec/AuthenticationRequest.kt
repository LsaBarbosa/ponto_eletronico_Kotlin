package br.com.santanna.ponto_eletronico.infrastructure.security.configsec

data class AuthenticationRequest(val cpf: String,
                                 val password: String)

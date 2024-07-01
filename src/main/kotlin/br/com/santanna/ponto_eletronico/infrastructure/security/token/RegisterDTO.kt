package br.com.santanna.ponto_eletronico.infrastructure.security.token

import br.com.santanna.ponto_eletronico.domain.entity.EmployeeRole

data  class RegisterDTO (val cpf:String, val password:String, val role: EmployeeRole)

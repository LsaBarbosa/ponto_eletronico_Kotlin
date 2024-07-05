package br.com.santanna.ponto_eletronico.domain.dto.employee

import br.com.santanna.ponto_eletronico.domain.entity.EmployeeRole
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.br.CPF


data class UpdateEmployeeDto(
      var salary: Double? = null,

      @field:Size(min = 1, max = 100, message = "A posição deve ter entre 1 e 100 caracteres")
      var position: String? = null,

      @field:CPF(message = "O CPF deve ser válido")
      @field:Size(min = 11, max = 11, message = "O CPF deve ter exatamente 11 caracteres")
      var cpf: String? = null,

      @field:Size(min = 8, max = 8, message = "A senha deve ter exatamente 8 caracteres")
      var passwords: String? = null,

      var role: EmployeeRole? = null
)


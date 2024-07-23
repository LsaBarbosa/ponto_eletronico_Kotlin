package br.com.santanna.ponto_eletronico.domain.dto.employee

import br.com.santanna.ponto_eletronico.domain.entity.employee.EmployeeRole
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.br.CNPJ
import java.util.*


data class EmployeeDto (
      var id: UUID? = null,

      @field:NotBlank(message = "O nome não pode estar em branco")
      var name: String? = null,

      @field:NotBlank(message = "O sobrenome não pode estar em branco")
      var surname: String? = null,

      @field:NotNull(message = "O salário não pode estar em branco")
      var salary: Double? = null,

      @field:NotBlank(message = "A posição não pode estar em branco")
      @field:Size(min = 1, max = 100, message = "A posição deve ter entre 1 e 100 caracteres")
      var position: String? = null,

      @field:Email(message = "Email é obrigatório")
      var email: String? = null,

      @field:Size(min = 11, max = 11, message = "O CPF deve ter exatamente 11 caracteres")
      var cpf: String? = null,

      @field:NotBlank(message = "A senha não pode estar em branco")
      @field:Size(min = 8, max = 50, message = "A senha deve ter no mínimo 8 caracteres")
      var passwords: String? = null,

      @field:NotBlank(message = "A CNPJ não pode estar em branco")
      @field:CNPJ(message = "A CNPJ deve estar no formato correto com 14 digitos")
      var companyCNPJ: String? = null,
      var role: EmployeeRole? = null
)


package br.com.santanna.ponto_eletronico.domain.dto.employee

import br.com.santanna.ponto_eletronico.domain.entity.EmployeeRole


data class UpdateEmployeeDto(
      var salary: Double? = null,
      var position: String? = null,
      var cpf: String? = null,
      var passwords: String? = null,
      var role: EmployeeRole? = null
)


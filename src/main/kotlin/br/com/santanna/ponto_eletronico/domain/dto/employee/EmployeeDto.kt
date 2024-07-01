package br.com.santanna.ponto_eletronico.domain.dto.employee

import br.com.santanna.ponto_eletronico.domain.entity.EmployeeRole


data class EmployeeDto (
      var id: Long? = null,
      var name:String? = null,
      var surname:String? = null,
      var salary:Double? = null,
      var position:String? = null,
      var cpf:String? = null,
      var passwords:String? = null,
      var companyName:String? = null,
      var role: EmployeeRole?= null
)


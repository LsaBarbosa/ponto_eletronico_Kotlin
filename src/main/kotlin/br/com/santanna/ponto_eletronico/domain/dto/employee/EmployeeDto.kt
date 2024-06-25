package br.com.santanna.ponto_eletronico.domain.dto.employee


data class EmployeeDto (
      var id: Long? = null,
      var name:String? = null,
      var surname:String? = null,
      var salary:Double? = null,
      var position:String? = null,
      var password:String? = null,
      var companyName:String? = null,
)


package br.com.santanna.ponto_eletronico.domain.dto.timeRecord

import org.hibernate.validator.constraints.br.CPF

data class BalanceHoursDto (
    @field:CPF
    val employeeCpf:String? = null,
    val balance:String? = null
)

package br.com.santanna.ponto_eletronico.domain.dto.employee

import org.hibernate.validator.constraints.br.CPF

data class ManagerEmployeeRequestByCPFDto(
    @field:CPF
    val employeeCpf: String
)

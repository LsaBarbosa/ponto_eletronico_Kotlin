package br.com.santanna.ponto_eletronico.domain.dto.employee

import java.util.*

data class SimpleEmployeeDto(
    var id: UUID? = null,
    var name: String? = null,
    var surname: String? = null,
    var salary: Double? = null,
    var position: String? = null,
)

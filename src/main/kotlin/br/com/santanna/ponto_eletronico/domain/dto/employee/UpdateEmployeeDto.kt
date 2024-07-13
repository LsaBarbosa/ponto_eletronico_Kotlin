package br.com.santanna.ponto_eletronico.domain.dto.employee

import br.com.santanna.ponto_eletronico.domain.entity.EmployeeRole
import jakarta.validation.constraints.Size


data class UpdateEmployeeDto(
    var salary: Double? = null,
    var name: String? = null,
    var surname: String? = null,
    @field:Size(min = 1, max = 100, message = "A posição deve ter entre 1 e 100 caracteres")
    var position: String? = null,
    var role: EmployeeRole? = null
)


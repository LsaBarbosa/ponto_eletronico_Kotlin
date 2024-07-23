package br.com.santanna.ponto_eletronico.domain.dto.company

import br.com.santanna.ponto_eletronico.domain.dto.employee.SimpleEmployeeDto
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CompanyDTO(
    var id: Long? = null,
    @field:NotBlank(message = "O nome da empresa não pode estar em branco")
    var nameCompany: String? = null,
    var address: AddressDTO? = null,
    @field:Size(min = 14, max = 14, message = "O CNPJ deve ter exatamente 14 caracteres")
    var companyCNPJ: String? = null,
    val employees: List<SimpleEmployeeDto>? = ArrayList(),
)

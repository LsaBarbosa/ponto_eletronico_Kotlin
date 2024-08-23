package br.com.santanna.ponto_eletronico.domain.dto.company

import jakarta.validation.constraints.NotBlank

data class AddressDTO(
    var street: String? = null,
    var city: String? = null,
    var state: String? = null,
    var number: String? = null,
    @field:NotBlank(message = "Campo CEP deve ser preenchido")
    var postalCode: String
)

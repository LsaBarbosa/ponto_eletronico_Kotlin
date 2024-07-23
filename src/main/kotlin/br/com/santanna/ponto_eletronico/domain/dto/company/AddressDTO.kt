package br.com.santanna.ponto_eletronico.domain.dto.company

data class AddressDTO(
    var street: String? = null,
    var city: String? = null,
    var state: String? = null,
    var number: String? = null,
    var postalCode: String? = null,
)

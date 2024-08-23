package br.com.santanna.ponto_eletronico.domain.dto.company

data class CompanyDTO(
    var id: Long? = null,
    var nameCompany: String? = null,
    var companyCNPJ: String? = null,
    var address: AddressDTO? = null

    )

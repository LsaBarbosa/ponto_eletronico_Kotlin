package br.com.santanna.ponto_eletronico.domain.dto.company

import java.util.UUID

data class CompanyDTO(
    var id: UUID? = null,
    var nameCompany: String? = null,
    var companyCNPJ: String? = null,
    var address: AddressDTO? = null

    )

package br.com.santanna.ponto_eletronico.domain.entity.company

import jakarta.persistence.Embeddable

@Embeddable
class Address (
    var street: String? = null,
    var city: String? = null,
    var state: String? = null,
    var postalCode: String? = null,
    var number:String?=null

)
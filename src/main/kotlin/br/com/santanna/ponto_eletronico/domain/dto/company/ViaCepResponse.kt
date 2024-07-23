package br.com.santanna.ponto_eletronico.domain.dto.company

data class ViaCepResponse (
    val logradouro: String?,
    val localidade: String?,
    val uf: String?,
    val cep: String?
) {
    fun toAddressDTO() = AddressDTO(
        street = this.logradouro,
        city = this.localidade,
        state = this.uf,
        postalCode = this.cep
    )
}
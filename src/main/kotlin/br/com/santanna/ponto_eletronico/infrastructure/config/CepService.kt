package br.com.santanna.ponto_eletronico.infrastructure.config

import br.com.santanna.ponto_eletronico.domain.dto.company.AddressDTO
import br.com.santanna.ponto_eletronico.domain.dto.company.ViaCepResponse
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class CepService(private val restTemplate: RestTemplate) {

    fun getEnderecoByCep(cep: String): AddressDTO {
        val url = "https://viacep.com.br/ws/$cep/json/"
        val response = restTemplate.getForObject(url, ViaCepResponse::class.java)
        return response?.toAddressDTO() ?: throw IllegalArgumentException("CEP inválido ou não encontrado")
    }
}
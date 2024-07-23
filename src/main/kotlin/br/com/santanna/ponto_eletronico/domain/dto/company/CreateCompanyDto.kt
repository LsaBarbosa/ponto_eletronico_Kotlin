package br.com.santanna.ponto_eletronico.domain.dto.company

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreateCompanyDto(
    @field:NotBlank(message = "O nome da empresa não pode estar em branco")
    var nameCompany: String,
    @field:Size(min = 14, max = 14, message = "O CNPJ deve ter exatamente 14 caracteres")
    var companyCNPJ: String,
    @field:NotBlank(message = "O nome do gerente não pode estar em branco")
    var managerName: String,
    @field:NotBlank(message = "O sobrenome do gerente não pode estar em branco")
    var managerSurname: String,
    @field:NotNull(message = "O email do gerente não pode estar em branco")
    var managerEmail:String,
    @field:Size(min = 1, max = 100, message = "A posição deve ter entre 1 e 100 caracteres")
    var managerPosition: String,
    @field:Size(min = 11, max = 11, message = "O CPF do gerente deve ter exatamente 11 caracteres")
    var managerCpf: String,
    @field:NotBlank(message = "A senha do gerente não pode estar em branco")
    @field:Size(min = 8, max = 50, message = "A senha do gerente deve ter no mínimo 8 caracteres")
    var managerPasswords: String,

    var address: AddressDTO? = null,
)

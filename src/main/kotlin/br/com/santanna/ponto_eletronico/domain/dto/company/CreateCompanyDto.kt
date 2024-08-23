package br.com.santanna.ponto_eletronico.domain.dto.company

import jakarta.validation.constraints.NotBlank

data class CreateCompanyDto(
    @field:NotBlank(message = "O nome da empresa é obrigatório")
    var nameCompany: String,

    @field:NotBlank(message = "O CNPJ da empresa é obrigatório")
    var companyCNPJ: String,

    @field:NotBlank(message = "O nome do gerente é obrigatório")
    var managerName: String,

    @field:NotBlank(message = "O sobrenome do gerente é obrigatório")
    var managerSurname: String,

    @field:NotBlank(message = "O cargo do gerente é obrigatório")
    var managerPosition: String,

    @field:NotBlank(message = "O CPF do gerente é obrigatório")
    var managerCpf: String,

    @field:NotBlank(message = "O e-mail do gerente é obrigatório")
    var managerEmail: String,

    @field:NotBlank(message = "A senha do gerente é obrigatória")
    var managerPasswords: String,

    var address: AddressDTO,
)

package br.com.santanna.ponto_eletronico.domain.dto.image

data class DeleteImageRequestDto(
    val imageId: Long,
    val employeeCpfTarget: String,
    val employeeManagerCpf: String,
    val passwords: String
)

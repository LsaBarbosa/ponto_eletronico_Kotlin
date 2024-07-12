package br.com.santanna.ponto_eletronico.domain.dto.employee

data class DeleteEmployeeRequestDto(
    val employeeCpfTarget: String,
    val employeeManagerCpf: String,  
    val passwords: String
)

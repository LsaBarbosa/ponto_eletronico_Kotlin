package br.com.santanna.ponto_eletronico.domain.dto.timeRecord

data class UpdateTimeRecordRequestDto(
    val timeRecordId: Long,
    val employeeCpfTarget: String,
    val employeeManagerCpf: String,
    val passwords: String,
    val updateTimeRecordDto: UpdateTimeRecordDto
)

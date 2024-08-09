package br.com.santanna.ponto_eletronico.domain.dto.timeRecord

import java.util.*

data class DeleteTimeRecordRequestDto(
    val timeRecordId: Long,
    val employeeIdTarget: UUID,
    val passwords: String
)

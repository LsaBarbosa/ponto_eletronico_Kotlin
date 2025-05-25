package br.com.santanna.ponto_eletronico.domain.dto.timeRecord

import br.com.santanna.ponto_eletronico.domain.entity.timerecord.TimeRecordStatus
import java.util.*

data class CreateTimeRecordByRangeRequest(
    val employeeId: UUID,
    val startDate: String, // "dd-MM-yyyy"
    val endDate: String,   // "dd-MM-yyyy"
    val status: TimeRecordStatus
)

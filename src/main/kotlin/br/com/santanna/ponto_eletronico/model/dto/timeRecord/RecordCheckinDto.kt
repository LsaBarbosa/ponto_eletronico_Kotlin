package br.com.santanna.ponto_eletronico.model.dto.timeRecord

import br.com.santanna.ponto_eletronico.model.TimeRecord
import java.time.format.DateTimeFormatter

data class RecordCheckinDto (
    val id: Long? = null,
    val startOfWorkTime: String? = null,
    val startOfWorkDate: String? = null
) {
    constructor(dto: TimeRecord) : this(
        id = dto.id,
        startOfWorkTime = dto.startWorkTime?.toLocalTime()?.format(DateTimeFormatter.ofPattern("HH:mm")),
        startOfWorkDate = dto.startWorkTime?.toLocalDate()?.format(DateTimeFormatter.ISO_DATE)
    )
}
package br.com.santanna.domain.dto.timeRecord

import br.com.santanna.domain.entity.TimeRecord
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
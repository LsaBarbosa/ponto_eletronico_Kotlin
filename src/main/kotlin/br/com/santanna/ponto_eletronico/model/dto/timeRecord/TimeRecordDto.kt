package br.com.santanna.ponto_eletronico.model.dto.timeRecord

import java.time.LocalDateTime

data class TimeRecordDto(val id: Long?,
                         val startWorkTime: LocalDateTime?,
                         val endWorkTime: LocalDateTime?,
                         val timeWorked: Long?)

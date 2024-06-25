package br.com.santanna.ponto_eletronico.domain.dto.timeRecord

data class DetailedTimeRecordDto(
    val id: Long? = null,
    val startWorkTime: String? = null,
    val endWorkTime: String? = null,
    val startWorkDate: String? = null,
    val endWorkDate: String? = null,
    val timeWorked: String? = null,
)

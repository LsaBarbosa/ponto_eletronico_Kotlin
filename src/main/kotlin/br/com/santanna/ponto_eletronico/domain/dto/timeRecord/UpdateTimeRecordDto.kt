package br.com.santanna.ponto_eletronico.domain.dto.timeRecord

data class UpdateTimeRecordDto(
    var id: Long,
    val startWorkDate: String?,
    val startWorkTime: String?,
    val endWorkDate: String?,
    val endWorkTime: String?
)

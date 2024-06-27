package br.com.santanna.domain.dto.timeRecord

data class UpdateTimeRecordDto(
    var id: Long,
    val startWorkDate: String?,
    val startWorkTime: String?,
    val endWorkDate: String?,
    val endWorkTime: String?
)

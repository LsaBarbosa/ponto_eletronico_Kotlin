package br.com.santanna.ponto_eletronico.domain.dto.timeRecord

data class RecordCheckoutDto(
    val id: Long? = null,
    val endWorkTime: String? = null,
    val endWorkDate: String? = null,
    val timeWorked: String? = null,
    )

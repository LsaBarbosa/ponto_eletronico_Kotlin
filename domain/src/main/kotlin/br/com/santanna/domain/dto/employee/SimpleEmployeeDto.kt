package br.com.santanna.domain.dto.employee

data class SimpleEmployeeDto(
    var id: Long? = null,
    var name: String? = null,
    var surname: String? = null,
    var salary: Double? = null,
    var position: String? = null,
)
package br.com.santanna.ponto_eletronico.domain.dto.employee

import br.com.santanna.ponto_eletronico.domain.dto.company.CompanyGetDto
import br.com.santanna.ponto_eletronico.domain.dto.timeRecord.TimeRecordDto

data class EmployeeGetDto(
    var id: Long? = null,
    var name: String? = null,
    var surname: String? = null,
    var salary: Double? = null,
    var position: String? = null,
    var password: String? = null,
    val timeWorked: List<TimeRecordDto>? = ArrayList(),
    val company: CompanyGetDto? = null
)



package br.com.santanna.domain.entity

import java.time.LocalDateTime

data class TimeRecord(

    var id: Long? = null,
    var startWorkTime: LocalDateTime? = null,
    var endWorkTime: LocalDateTime? = null,
    var timeWorked: Long? = null,
    var employee: Employee? = null
)

package br.com.santanna.domain.entity


data class Employee(

    var id: Long? = null,
    var name: String? = null,
    var surname: String? = null,
    var salary: Double? = null,
    var position: String? = null,
    var password: String? = null,
    var timeWorked: List<TimeRecord?> = ArrayList(),
    var company: Company? = null
)
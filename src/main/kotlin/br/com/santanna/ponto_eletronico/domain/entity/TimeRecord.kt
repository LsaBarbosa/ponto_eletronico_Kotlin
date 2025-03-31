package br.com.santanna.ponto_eletronico.domain.entity

import br.com.santanna.ponto_eletronico.domain.entity.employee.Employee
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity

data class TimeRecord(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long  = 0,
    var startWorkTime: LocalDateTime? = null,
    var endWorkTime: LocalDateTime? = null,
    var timeWorked: Long? = null,
    var edited: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    @JsonIgnoreProperties("timeWorked")
    var employee: Employee? = null
)

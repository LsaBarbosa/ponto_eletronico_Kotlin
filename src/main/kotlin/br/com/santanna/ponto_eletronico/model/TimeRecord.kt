package br.com.santanna.ponto_eletronico.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "tb_time_record")
data class TimeRecord(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var startWorkTime: LocalDateTime? = null,
    var endWorkTime: LocalDateTime? = null,
    var timeWorked: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    @JsonIgnoreProperties("timeWorked")
    var employee: Employee? = null
)

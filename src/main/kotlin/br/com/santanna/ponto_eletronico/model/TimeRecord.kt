package br.com.santanna.ponto_eletronico.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class TimeRecord(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var startWorkTime: LocalDateTime? = null,
    var endWorkTime: LocalDateTime? = null,
    var workTime:Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties("TimeRecord")
    var employee: Employee? = null
)

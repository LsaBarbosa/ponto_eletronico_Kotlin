package br.com.santanna.ponto_eletronico.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*

@Entity
data class Employee(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String? = null,
    var surname: String? = null,
    var salary: Double? = null,
    var position: String? = null,
    var password: String? = null,

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JsonIgnoreProperties("employee")
    var timeWorked: List<TimeRecord?> = ArrayList(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    var company: Company? = null
)
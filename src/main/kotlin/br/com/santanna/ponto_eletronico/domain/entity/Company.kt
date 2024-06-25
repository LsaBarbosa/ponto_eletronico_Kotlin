package br.com.santanna.ponto_eletronico.domain.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*

@Entity
data class Company (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var nameCompany:String? = null,
    var companyCNPJ: String? = null,

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JsonIgnoreProperties("timeWorked")
    var employees: List<Employee?> = ArrayList()

)

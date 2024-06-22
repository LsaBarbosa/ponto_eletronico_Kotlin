package br.com.santanna.ponto_eletronico.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
data class Company (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var nameCompany:String? = null,
    var companyCNPJ: String? = null,

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    @JsonIgnore
    var employees:List<Employee?> = ArrayList()

)

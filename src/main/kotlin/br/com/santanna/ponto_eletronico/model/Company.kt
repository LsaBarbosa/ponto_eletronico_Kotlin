package br.com.santanna.ponto_eletronico.model

import jakarta.persistence.*

@Entity
data class Company (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var nameCompany:String? = null,
    var companyCNPJ: String? = null,

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var employees: List<Employee?> = ArrayList()

)

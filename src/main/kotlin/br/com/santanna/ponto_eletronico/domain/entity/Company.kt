package br.com.santanna.ponto_eletronico.domain.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.br.CNPJ

@Entity
@Table(name = "company", uniqueConstraints = [
    UniqueConstraint(columnNames = ["nameCompany"]),
    UniqueConstraint(columnNames = ["companyCNPJ"])
])
data class Company (

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,



    var nameCompany:String? = null,


    @Column(nullable = false, unique = true)
    var companyCNPJ: String? = null,

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonIgnoreProperties("timeWorked")
    var employees: List<Employee?> = ArrayList()

)

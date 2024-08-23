package br.com.santanna.ponto_eletronico.domain.entity.company

import br.com.santanna.ponto_eletronico.domain.entity.employee.Employee
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "company", uniqueConstraints = [
    UniqueConstraint(columnNames = ["nameCompany"]),
    UniqueConstraint(columnNames = ["companyCNPJ"])
])
data class Company (

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID? = null,

    @Column(nullable = false)
    var nameCompany: String,

    @Column(nullable = false, unique = true)
    var companyCNPJ: String,

    @Embedded
    var address: Address,

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonIgnoreProperties("timeWorked")
    var employees: List<Employee> = ArrayList()
){
    constructor() : this(
        id = null,
        nameCompany = "",
        companyCNPJ = "",
        address = Address(),
        employees = ArrayList()
    )
}

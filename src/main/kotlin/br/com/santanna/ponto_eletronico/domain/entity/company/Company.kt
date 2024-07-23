package br.com.santanna.ponto_eletronico.domain.entity.company

import br.com.santanna.ponto_eletronico.domain.entity.employee.Employee
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*

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
    @Embedded
    var address: Address? = null,
    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonIgnoreProperties("timeWorked")
    var employees: List<Employee?> = ArrayList()

)

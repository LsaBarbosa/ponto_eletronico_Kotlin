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

    @field:NotBlank(message = "O nome da empresa não pode estar em branco")
    @Column(nullable = false, unique = true)
    var nameCompany:String? = null,

    @field:Size(min = 14, max = 14, message = "O CNPJ deve ter exatamente 14 caracteres")
    @field:CNPJ(message = "A CNPJ deve estar no formato correto com 14 digitos")
    @Column(nullable = false, unique = true)
    var companyCNPJ: String? = null,

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonIgnoreProperties("timeWorked")
    var employees: List<Employee?> = ArrayList()

)

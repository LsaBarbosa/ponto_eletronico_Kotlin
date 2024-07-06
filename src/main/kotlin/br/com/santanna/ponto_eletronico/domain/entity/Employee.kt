package br.com.santanna.ponto_eletronico.domain.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.br.CPF
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
data class Employee(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @NotBlank
    var name: String? = null,
    @NotBlank
    var surname: String? = null,
    @NotNull
    var salary: Double? = null,
    @NotBlank
    @Size(min = 1, max = 100)
    var position: String? = null,
    @NotBlank
   @Size(min = 8, max = 8)
    var passwords: String? = null,

    @CPF
    @Size(min = 11, max = 11)
    @Column(unique = true)
    var cpf: String? = null,

    @Enumerated(EnumType.STRING)
    var role: EmployeeRole?=null,

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JsonIgnoreProperties("employee")
    var timeWorked: List<TimeRecord?> = ArrayList(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    @NotNull
    var company: Company? = null,

    var imagePath: String? = null


): UserDetails {
    constructor() : this(null, null, null, null, null, null, null, EmployeeRole.USER, emptyList(), null)


    override fun getAuthorities(): Collection<GrantedAuthority> {
        return when (this.role) {
            EmployeeRole.ADMIN -> listOf(
                SimpleGrantedAuthority("ROLE_ADMIN"),
                SimpleGrantedAuthority("ROLE_MANAGER"),
                SimpleGrantedAuthority("ROLE_USER")
            )
            EmployeeRole.MANAGER -> listOf(
                SimpleGrantedAuthority("ROLE_MANAGER"),
                SimpleGrantedAuthority("ROLE_USER")
            )
            else -> listOf(SimpleGrantedAuthority("ROLE_USER"))
        }
    }

    override fun getPassword(): String? =passwords

    override fun getUsername(): String? = cpf

    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true
}
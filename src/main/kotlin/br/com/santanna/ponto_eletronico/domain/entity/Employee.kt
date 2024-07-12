package br.com.santanna.ponto_eletronico.domain.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
data class Employee(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,


    var name: String? = null,

    var surname: String? = null,

    var salary: Double? = null,

    var position: String? = null,

    var passwords: String? = null,


    var cpf: String? = null,

    @Enumerated(EnumType.STRING)
    var role: EmployeeRole?=null,

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JsonIgnoreProperties("employee")
    var timeWorked: List<TimeRecord?> = ArrayList(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")

    var company: Company? = null,

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var images: List<Image>? = null


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
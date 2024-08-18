package br.com.santanna.ponto_eletronico.domain.entity.employee

import br.com.santanna.ponto_eletronico.domain.entity.TimeRecord
import br.com.santanna.ponto_eletronico.domain.entity.company.Company
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

@Entity
data class Employee(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID? = null,

    var name: String? = "",

    var surname: String? = "",

    var salary: Double? = 0.0,

    var position: String? = "",

    var passwords: String? = "",

    var email: String? = "",

    var cpf: String? = "",

    @Enumerated(EnumType.STRING)
    var role: EmployeeRole? = EmployeeRole.USER,

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JsonIgnoreProperties("employee")
    var timeWorked: List<TimeRecord> = listOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    var company: Company? = null,

) : UserDetails {

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

    override fun getPassword(): String? = passwords

    override fun getUsername(): String? = cpf

    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true
}
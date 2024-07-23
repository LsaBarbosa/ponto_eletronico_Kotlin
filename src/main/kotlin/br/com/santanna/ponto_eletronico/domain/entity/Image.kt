package br.com.santanna.ponto_eletronico.domain.entity

import br.com.santanna.ponto_eletronico.domain.entity.employee.Employee
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDate

@Entity
data class Image(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var filePath: String? = null,

    @Column(nullable = false)
    var uploadDate: LocalDate? = null,

    @NotBlank
    @Size(min = 1, max = 250)
    @Column(nullable = true)
    var message: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    var employee: Employee? = null
)
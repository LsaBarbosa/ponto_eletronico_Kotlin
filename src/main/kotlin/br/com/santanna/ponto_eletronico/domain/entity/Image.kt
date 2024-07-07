package br.com.santanna.ponto_eletronico.domain.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Entity
data class Image(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var filePath: String? = null,

    @NotBlank
    @Size(min = 1, max = 250)
    @Column(nullable = true)
    var message: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    var employee: Employee? = null
)
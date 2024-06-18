package br.com.santanna.ponto_eletronico.modelo

import jakarta.persistence.*
import org.jetbrains.annotations.NotNull

@Entity
@Table(name = "tb_employee")
data class Employee (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name:String? = null,
    var password:String? = null
)
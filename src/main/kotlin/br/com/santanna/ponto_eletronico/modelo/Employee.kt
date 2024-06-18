package br.com.santanna.ponto_eletronico.modelo

import jakarta.persistence.*

@Entity
@Table(name = "tb_employee")
data class Employee (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name:String? = null,
    var password:String? = null,

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    var workTime:List<TimeRecord?> = ArrayList()
)
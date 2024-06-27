package br.com.santanna.infrastructure.model

import br.com.santanna.domain.entity.Employee
import jakarta.persistence.*


@Entity
data class EmployeeModel(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String? = null,
    var surname: String? = null,
    var salary: Double? = null,
    var position: String? = null,
    var password: String? = null,

    @OneToMany(mappedBy = "employeeModel", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var timeWorkedModel: List<TimeRecordModel?> = ArrayList(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    var companyModel: CompanyModel? = null
)

fun EmployeeModel .toEmployee(): Employee {
    return Employee(
        id = this.id,
       name = this.name,
        surname = this.surname,
        salary = this.salary,
        position = this.position,
        password = this.password,
        timeWorked = this.timeWorkedModel.mapNotNull { it?.toTimeRecord() },
        company = this.companyModel?.toCompany()

    )
}
fun Employee.toEmployeeModel():EmployeeModel {
    return EmployeeModel(
        id = this.id,
        name = this.name,
        surname = this.surname,
        salary = this.salary,
        position = this.position,
        password = this.password,
        timeWorkedModel = timeWorked.mapNotNull { it?.TimeRecordModel() },
        companyModel = company?.toCompanyModel()
    )
}

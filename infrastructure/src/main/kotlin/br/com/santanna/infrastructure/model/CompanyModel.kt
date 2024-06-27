package br.com.santanna.infrastructure.model

import br.com.santanna.domain.entity.Company
import jakarta.persistence.*

@Entity
data class CompanyModel (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var nameCompany:String? = null,
    var companyCNPJ: String? = null,

    @OneToMany(mappedBy = "companyModel", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var employees: List<EmployeeModel?> = ArrayList()

)
fun CompanyModel.toCompany():Company{
    return Company(
        id = this.id,
        nameCompany = this.nameCompany,
        companyCNPJ = this.companyCNPJ,
        employees = this.employees.mapNotNull { it?.toEmployee() }
    )
}
fun Company.toCompanyModel(): CompanyModel {
    return CompanyModel(
        id = this.id,
        nameCompany = this.nameCompany,
        companyCNPJ = this.companyCNPJ,
        employees = this.employees.mapNotNull { it?.toEmployeeModel() }
    )
}

package br.com.santanna.domain.entity


 class Company(

    var id: Long? = null,
    var nameCompany:String? = null,
    var companyCNPJ: String? = null,
    var employees: List<Employee?> = ArrayList()

)


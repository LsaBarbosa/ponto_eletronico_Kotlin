package br.com.santanna.ponto_eletronico.domain.dataprovider

import br.com.santanna.ponto_eletronico.domain.entity.employee.Employee
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

interface EmployeeDataProvider {


    fun save(employee: Employee): Employee
    fun findById(id: UUID): Employee
    fun findByNameAndSurnameIgnoreCase(name: String?, surname: String?): Employee?
    fun findByCpf(cpf: String): UserDetails?
    fun findCpf(cpf: String?): Employee?
    fun deleteById(id: UUID)
    fun findByCompany(companyId: Long, pageable: Pageable): Page<Employee>
}
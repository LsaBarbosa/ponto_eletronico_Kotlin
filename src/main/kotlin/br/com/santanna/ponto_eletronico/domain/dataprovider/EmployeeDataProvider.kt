package br.com.santanna.ponto_eletronico.domain.dataprovider

import br.com.santanna.ponto_eletronico.domain.entity.employee.Employee
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface EmployeeDataProvider {


    fun save(employee: Employee): Employee
    fun findById(id: UUID): Employee
    fun findCpf(cpf: String?): Employee?
    fun deleteById(id: UUID)
    fun findByCompany(companyId: UUID, pageable: Pageable): Page<Employee>
}
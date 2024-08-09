package br.com.santanna.ponto_eletronico.infrastructure.repository

import br.com.santanna.ponto_eletronico.domain.entity.employee.Employee
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface EmployeeRepository: JpaRepository<Employee, UUID> {

    fun findByNameAndSurnameIgnoreCase(name: String?, surname: String?): Employee?
    fun findByCpfIgnoreCase(cpf: String?): Employee?
    fun findByCompanyId(companyId: Long, pageable: Pageable): Page<Employee>


}

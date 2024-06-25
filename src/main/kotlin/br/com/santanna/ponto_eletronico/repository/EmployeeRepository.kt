package br.com.santanna.ponto_eletronico.repository

import br.com.santanna.ponto_eletronico.model.Employee
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmployeeRepository: JpaRepository<Employee, Long> {

    fun existsByNameAndSurnameIgnoreCase(name: String?, surname: String?): Boolean
    fun findByNameAndSurnameIgnoreCase(name: String?, surname: String?): Employee?


}

package br.com.santanna.ponto_eletronico.repository

import br.com.santanna.ponto_eletronico.modelo.Employee
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmployeeRepository: JpaRepository<Employee, Long> {
    fun findByNameContainsIgnoreCase(name: String):Employee?
    fun deleteByName(name: String)

}

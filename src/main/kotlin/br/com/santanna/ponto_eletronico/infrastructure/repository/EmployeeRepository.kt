package br.com.santanna.ponto_eletronico.infrastructure.repository

import br.com.santanna.ponto_eletronico.domain.entity.Employee
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Repository

@Repository
interface EmployeeRepository: JpaRepository<Employee, Long> {


    fun findByCpf(cpf: String?): UserDetails?
    fun findByCpfIgnoreCase(cpf: String?): Employee?


}

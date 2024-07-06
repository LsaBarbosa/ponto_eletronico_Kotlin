package br.com.santanna.ponto_eletronico.domain.dataprovider

import br.com.santanna.ponto_eletronico.domain.entity.Employee
import org.springframework.security.core.userdetails.UserDetails

interface EmployeeDataProvider {

    fun findAll():  List<Employee>
    fun save(employee: Employee): Employee
    fun findById(id: Long): Employee
    fun findByNameAndSurnameIgnoreCase(name: String?, surname: String?): Employee?
    fun findByCpf(cpf: String): UserDetails?
    fun findCpf(cpf: String?): Employee?
    fun deleteByCpf(cpf: String)
}
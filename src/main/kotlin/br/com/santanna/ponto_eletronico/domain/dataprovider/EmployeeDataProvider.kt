package br.com.santanna.ponto_eletronico.domain.dataprovider

import br.com.santanna.ponto_eletronico.domain.entity.Employee

interface EmployeeDataProvider {
    fun existsByNameAndSurnameIgnoreCase(name: String?, surname: String?): Boolean
    fun findByNameAndSurnameIgnoreCase(name: String?, surname: String?): Employee?
    fun deleteByNameAndSurname(name: String?, surname: String?)
    fun findAll(): MutableList<Employee>
    fun save(employee: Employee): Employee
    fun findById(id: Long): Employee
}
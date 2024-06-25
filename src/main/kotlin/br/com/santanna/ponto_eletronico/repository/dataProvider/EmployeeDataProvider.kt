package br.com.santanna.ponto_eletronico.repository.dataProvider

import br.com.santanna.ponto_eletronico.model.Employee

interface EmployeeDataProvider {
    fun existsByNameAndSurnameIgnoreCase(name: String?, surname: String?): Boolean
    fun findByNameAndSurnameIgnoreCase(name: String?, surname: String?): Employee?
    fun deleteByNameAndSurname(name: String?, surname: String?)
    fun findAll(): MutableList<Employee>
    fun save(employee: Employee): Employee
    fun findById(id: Long): Employee
}
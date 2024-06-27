package br.com.santanna.domain.dataprovider

import br.com.santanna.domain.entity.Employee

interface EmployeeDataProvider {
    fun existsByNameAndSurnameIgnoreCase(name: String?, surname: String?): Boolean
    fun findByNameAndSurnameIgnoreCase(name: String?, surname: String?): Employee?
    fun deleteByNameAndSurname(name: String?, surname: String?)
    fun findAll(): List<Employee>
    fun save(employee: Employee): Employee
    fun findById(id: Long): Employee
}
package br.com.santanna.domain.service

import br.com.santanna.domain.dto.employee.EmployeeDto
import br.com.santanna.domain.dto.employee.EmployeeGetDto

interface EmployeeService {
    fun getAllEmployees(): List<EmployeeGetDto>
    fun getEmployeeById(id: Long): EmployeeGetDto?
    fun getEmployeeByNameAndSurname(name: String, surname: String): EmployeeGetDto?
    fun registerEmployee(employeeDto: EmployeeDto): EmployeeDto
    fun updateEmployee(employeeDto: EmployeeDto): EmployeeDto
    fun deleteEmployee(name: String, surname: String)
}
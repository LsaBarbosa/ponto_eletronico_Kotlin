package br.com.santanna.ponto_eletronico.service

import br.com.santanna.ponto_eletronico.model.dto.employee.EmployeeDto
import br.com.santanna.ponto_eletronico.model.dto.employee.EmployeeGetDto

interface EmployeeService {
    fun getAllEmployees(): List<EmployeeGetDto>
    fun getEmployeeById(id: Long): EmployeeGetDto?
    fun getEmployeeByNameAndSurname(name: String, surname: String): EmployeeGetDto?
    fun registerEmployee(employeeDto: EmployeeDto): EmployeeDto
    fun updateEmployee(employeeDto: EmployeeDto): EmployeeDto
    fun deleteEmployee(name: String, surname: String)
}
package br.com.santanna.ponto_eletronico.domain.service

import br.com.santanna.ponto_eletronico.domain.dto.employee.*
import br.com.santanna.ponto_eletronico.domain.entity.Employee
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface EmployeeService {
    fun getAllEmployees(pageable: Pageable): Page<EmployeeGetDto>
    fun getEmployeeById(id: Long): EmployeeGetDto?
    fun getEmployeeByCpf(cpf: String): EmployeeGetDto?
    fun getEmployeeEntityByCpf(cpf: String): Employee?
    fun getEmployeeByNameAndSurname(name: String, surname: String): EmployeeGetDto?
    fun registerEmployee(employeeDto: EmployeeDto?): EmployeeDto
    fun updateEmployee(updateEmployeeRequestDto: UpdateEmployeeRequestDto): UpdateEmployeeDto
    fun deleteEmployee(deleteEmployeeRequestDto: DeleteEmployeeRequestDto)


}
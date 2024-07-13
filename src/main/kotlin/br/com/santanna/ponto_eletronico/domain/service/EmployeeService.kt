package br.com.santanna.ponto_eletronico.domain.service

import br.com.santanna.ponto_eletronico.domain.dto.employee.*
import br.com.santanna.ponto_eletronico.domain.entity.Employee
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface EmployeeService {
    fun getEmployeesByManager(managerEmployeeRequestDto: ManagerEmployeeRequestDto, pageable: Pageable): Page<EmployeeGetDto>
    fun getEmployeeById(id: Long): EmployeeGetDto?
    fun getEmployeeByCpf(request: ManagerEmployeeRequestByCPFDto): EmployeeGetDto?
    fun getEmployeeEntityByCpf(cpf: String): Employee?
    fun getEmployeeByNameAndSurname(name: String, surname: String): EmployeeGetDto?
    fun registerEmployee(managerCpf: String, createEmployeeDto: CreateEmployeeDto): EmployeeDto
    fun updateEmployee(updateEmployeeRequestDto: UpdateEmployeeRequestDto): UpdateEmployeeDto
    fun deleteEmployee(deleteEmployeeRequestDto: DeleteEmployeeRequestDto)
    fun updatePassword(updatePasswordDto: UpdatePassword)
}
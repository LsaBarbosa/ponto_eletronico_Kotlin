package br.com.santanna.ponto_eletronico.domain.service

import br.com.santanna.ponto_eletronico.domain.dto.employee.*
import br.com.santanna.ponto_eletronico.domain.entity.employee.Employee
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface EmployeeService {
    fun getEmployeeEntityByCpf(cpf: String): Employee?
    fun getEmployeeById(): EmployeeGetDto?
    fun resetPassword(resetPasswordDto: ResetPasswordDto)
    fun updatePassword(updatePasswordDto: UpdatePassword)
    fun getEmployeesAsManager(  pageable: Pageable): Page<EmployeeGetDto>
    fun getEmployeeByCpfAsManager(request: ManagerEmployeeRequestByCPFDto): EmployeeGetDto?
    fun getEmployeeByNameAndSurnameAsManager(name: String, surname: String): EmployeeGetDto?
    fun registerEmployeeAsManager(createEmployeeDto: CreateEmployeeDto): EmployeeDto
    fun updateEmployeeAsManager(updateEmployeeRequestDto: UpdateEmployeeRequestDto): UpdateEmployeeDto
    fun deleteEmployeeAsManager(deleteEmployeeRequestDto: DeleteEmployeeRequestDto)
}
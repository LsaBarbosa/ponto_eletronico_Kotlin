package br.com.santanna.ponto_eletronico.domain.service

import br.com.santanna.ponto_eletronico.domain.dto.employee.*
import br.com.santanna.ponto_eletronico.domain.entity.employee.Employee
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface EmployeeService {

    fun getEmployeeById(): EmployeeGetDto?
    fun getEmployeeEntityById(employeeId: UUID): Employee?
    fun resetPassword(resetPasswordDto: ResetPasswordDto)
    fun updatePassword(updatePasswordDto: UpdatePassword)
    fun updateEmail(updateEmail: UpdateEmail)
    fun getEmployeesAsManager(  pageable: Pageable): Page<EmployeeGetDto>
    fun getEmployeeByIdAsManager(id: UUID): EmployeeGetDto
    fun registerEmployeeAsManager(createEmployeeDto: CreateEmployeeDto): EmployeeDto
    fun updateEmployeeAsManager(updateEmployeeRequestDto: UpdateEmployeeRequestDto): UpdateEmployeeDto
    fun deleteEmployeeAsManager(deleteEmployeeRequestDto: DeleteEmployeeRequestDto)
}
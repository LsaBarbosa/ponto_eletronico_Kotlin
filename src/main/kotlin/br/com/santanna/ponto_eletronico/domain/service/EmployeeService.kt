package br.com.santanna.ponto_eletronico.domain.service

import br.com.santanna.ponto_eletronico.domain.dto.employee.EmployeeDto
import br.com.santanna.ponto_eletronico.domain.dto.employee.EmployeeGetDto
import br.com.santanna.ponto_eletronico.domain.dto.employee.UpdateEmployeeDto
import org.springframework.web.multipart.MultipartFile

interface EmployeeService {
    fun getAllEmployees(): List<EmployeeGetDto>
    fun getEmployeeById(id: Long): EmployeeGetDto?
    fun getEmployeeByCpf(cpf: String): EmployeeGetDto?
    fun getEmployeeByNameAndSurname(name: String, surname: String): EmployeeGetDto?
    fun registerEmployee(employeeDto: EmployeeDto?): EmployeeDto
    fun updateEmployee(cpf :String,updateEmployeeDto: UpdateEmployeeDto): UpdateEmployeeDto
    fun deleteEmployee(cpf: String)
    fun updateEmployeeImage(cpf: String, file: MultipartFile): EmployeeGetDto
    fun getEmployeeImage(cpf: String): ByteArray

}
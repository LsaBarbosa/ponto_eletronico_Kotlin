package br.com.santanna.ponto_eletronico.service

import br.com.santanna.ponto_eletronico.modelo.Employee
import br.com.santanna.ponto_eletronico.modelo.dto.EmployeeDto
import br.com.santanna.ponto_eletronico.repository.EmployeeRepository
import org.springframework.stereotype.Service


@Service
class EmployeeService(private val employeeRepository: EmployeeRepository) {

    fun getAllEmployees(): List<Employee> {
        return employeeRepository.findAll()
    }

    fun getEmployeeById(id: Long): Employee? {
        return employeeRepository.findById(id).orElseThrow { Exception("not found") }
    }

    fun getEmployeeByName(name: String): Employee? {
        return employeeRepository.findByNameContainsIgnoreCase(name)
    }

    fun registerEmployee(employeeDto: EmployeeDto): EmployeeDto {
        findByName(employeeDto)
        val employeeEntity = convertToEntity(employeeDto)
        val savedEmployeeEntity = employeeRepository.save(employeeEntity)
        return convertToDto(savedEmployeeEntity)
    }

    fun updateEmployee(employeeDto: EmployeeDto): EmployeeDto {
        findByName(employeeDto)
        val employeeEntity = convertToEntity(employeeDto)
        employeeEntity.name = employeeDto.name ?: employeeEntity.name
        employeeEntity.password = employeeDto.password ?: employeeEntity.password
        val updatedEmployeeEntity = employeeRepository.save(employeeEntity)
        return convertToDto(updatedEmployeeEntity)
    }

    fun deleteEmployee(name: String) {
            val employeeToDelete = employeeRepository.findByNameContainsIgnoreCase(name) ?:
            throw Exception("not found")
            employeeRepository.delete(employeeToDelete)
    }

    private fun findByName(employeeDto: EmployeeDto) {
        val employee: Employee? = employeeDto.name?.let {
            employeeRepository.findByNameContainsIgnoreCase(it)
        }

        if (employee != null && employee.id == employeeDto.id) {
            throw Exception("Employee with the same name already exists.")
        }
    }


    private fun convertToEntity(employeeDto: EmployeeDto): Employee {
        return Employee(
            id = employeeDto.id,
            name = employeeDto.name,
            password = employeeDto.password
        )
    }

    private fun convertToDto(employee: Employee): EmployeeDto {
        return EmployeeDto(
            id = employee.id,
            name = employee.name,
            password = employee.password

        )
    }
}


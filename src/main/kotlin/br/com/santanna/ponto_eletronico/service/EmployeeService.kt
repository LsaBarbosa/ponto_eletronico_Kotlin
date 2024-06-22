package br.com.santanna.ponto_eletronico.service

import br.com.santanna.ponto_eletronico.model.Employee
import br.com.santanna.ponto_eletronico.model.dto.employee.EmployeeDto
import br.com.santanna.ponto_eletronico.repository.CompanyRepository
import br.com.santanna.ponto_eletronico.repository.EmployeeRepository
import org.springframework.stereotype.Service


@Service
class EmployeeService(private val employeeRepository: EmployeeRepository,private val companyRepository: CompanyRepository) {

    fun getAllEmployees(): List<Employee> {
        return employeeRepository.findAll()
    }

    fun getEmployeeById(id: Long): Employee? {
        return employeeRepository.findById(id).orElseThrow { Exception("not found") }
    }

    fun getEmployeeByNameAndSurname(name: String, surname: String): Employee? {
        return employeeRepository.findByNameAndSurnameIgnoreCase(name, surname)
            ?: throw Exception("Employee not found with name: $name and surname: $surname")
    }

    fun registerEmployee(employeeDto: EmployeeDto): EmployeeDto {
        if (employeeRepository.existsByNameAndSurnameIgnoreCase(employeeDto.name!!, employeeDto.surname!!)) {
            throw  Exception("Employee with the same name already exists.")
        }

        val company = companyRepository.findByNameCompanyContainsIgnoreCase(employeeDto.companyName)

        val employeeEntity = Employee(
            name = employeeDto.name,
            surname = employeeDto.surname,
            salary = employeeDto.salary,
            position = employeeDto.position,
            company = company
        )
        val savedEmployeeEntity = employeeRepository.save(employeeEntity)
        return convertToDto(savedEmployeeEntity)
    }

    fun updateEmployee(employeeDto: EmployeeDto): EmployeeDto {
        val existingEmployeeEntity =
            employeeRepository.findByNameAndSurnameIgnoreCase(employeeDto.name!!, employeeDto.surname!!)
                ?: throw Exception("Employee not found with name: ${employeeDto.name} and surname: ${employeeDto.surname}")

        existingEmployeeEntity.salary = employeeDto.salary ?: existingEmployeeEntity.salary
        existingEmployeeEntity.password = employeeDto.password?: existingEmployeeEntity.password
        existingEmployeeEntity.position = employeeDto.position ?: existingEmployeeEntity.position

        val updatedEmployeeEntity = employeeRepository.save(existingEmployeeEntity)
        return convertToDto(updatedEmployeeEntity)
    }

    fun deleteEmployee(name: String, surname: String) {
            val employeeToDelete = employeeRepository.findByNameAndSurnameIgnoreCase(name,surname) ?:
            throw Exception("not found")
            employeeRepository.delete(employeeToDelete)
    }



    private fun convertToDto(employee: Employee): EmployeeDto {
        return EmployeeDto(
            id = employee.id,
            name = employee.name,
            surname = employee.surname,
            position = employee.position,
            salary = employee.salary,
            companyName = employee.company?.nameCompany

        )
    }
}


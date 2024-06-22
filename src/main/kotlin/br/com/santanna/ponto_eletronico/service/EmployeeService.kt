package br.com.santanna.ponto_eletronico.service

import br.com.santanna.ponto_eletronico.model.Employee
import br.com.santanna.ponto_eletronico.model.dto.employee.EmployeeDto
import br.com.santanna.ponto_eletronico.model.dto.employee.EmployeeGetDto
import br.com.santanna.ponto_eletronico.repository.CompanyRepository
import br.com.santanna.ponto_eletronico.repository.EmployeeRepository
import br.com.santanna.ponto_eletronico.util.employee.EmployeeUtils
import org.springframework.stereotype.Service


@Service
class EmployeeService(private val employeeRepository: EmployeeRepository,private val companyRepository: CompanyRepository) {
    fun getAllEmployees(): List<EmployeeGetDto> {
        val employees = employeeRepository.findAll()
        return employees.mapNotNull { EmployeeUtils.convertToGetEmployeeDto(it) }
    }

    fun getEmployeeById(id: Long): EmployeeGetDto? {
        val employee = employeeRepository.findById(id).orElseThrow { Exception("not found") }
        return EmployeeUtils.convertToGetEmployeeDto(employee)
    }

    fun getEmployeeByNameAndSurname(name: String, surname: String): EmployeeGetDto? {
        val employee = employeeRepository.findByNameAndSurnameIgnoreCase(name, surname)
            ?: throw Exception("Employee not found with name: $name and surname: $surname")
        return EmployeeUtils.convertToGetEmployeeDto(employee)
    }

    fun registerEmployee(employeeDto: EmployeeDto): EmployeeDto {
        if (employeeRepository.existsByNameAndSurnameIgnoreCase(employeeDto.name!!, employeeDto.surname!!)) {
            throw Exception("Employee with the same name already exists.")
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
        return EmployeeUtils.convertToDto(savedEmployeeEntity)
    }

    fun updateEmployee(employeeDto: EmployeeDto): EmployeeDto {
        val existingEmployeeEntity =
            employeeRepository.findByNameAndSurnameIgnoreCase(employeeDto.name!!, employeeDto.surname!!)
                ?: throw Exception("Employee not found with name: ${employeeDto.name} and surname: ${employeeDto.surname}")

        existingEmployeeEntity.salary = employeeDto.salary ?: existingEmployeeEntity.salary
        existingEmployeeEntity.password = employeeDto.password ?: existingEmployeeEntity.password
        existingEmployeeEntity.position = employeeDto.position ?: existingEmployeeEntity.position

        val updatedEmployeeEntity = employeeRepository.save(existingEmployeeEntity)
        return EmployeeUtils.convertToDto(updatedEmployeeEntity)
    }

    fun deleteEmployee(name: String, surname: String) {
        EmployeeUtils.deleteEmployee(employeeRepository, name, surname)
    }
}

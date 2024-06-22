package br.com.santanna.ponto_eletronico.util.employee

import br.com.santanna.ponto_eletronico.model.Company
import br.com.santanna.ponto_eletronico.model.Employee
import br.com.santanna.ponto_eletronico.model.TimeRecord
import br.com.santanna.ponto_eletronico.model.dto.company.CompanyGetDto
import br.com.santanna.ponto_eletronico.model.dto.employee.EmployeeDto
import br.com.santanna.ponto_eletronico.model.dto.employee.EmployeeGetDto
import br.com.santanna.ponto_eletronico.model.dto.timeRecord.TimeRecordDto
import br.com.santanna.ponto_eletronico.repository.EmployeeRepository

object EmployeeUtils {
    fun convertToDto(employee: Employee): EmployeeDto {
        return EmployeeDto(
            id = employee.id,
            name = employee.name,
            surname = employee.surname,
            position = employee.position,
            salary = employee.salary,
            companyName = employee.company?.nameCompany
        )
    }

    fun convertToGetEmployeeDto(employee: Employee): EmployeeGetDto? {
        val timeWorkedDtos = employee.timeWorked.map { convertToTimeRecordDto(it!!) }

        return EmployeeGetDto(
            id = employee.id,
            name = employee.name,
            surname = employee.surname,
            salary = employee.salary,
            position = employee.position,
            password = employee.password,
            timeWorked = timeWorkedDtos,
            company = employee.company?.convertToDto()
        )
    }

    fun convertToTimeRecordDto(timeRecord: TimeRecord): TimeRecordDto {
        return TimeRecordDto(
            id = timeRecord.id,
            startWorkTime = timeRecord.startWorkTime,
            endWorkTime = timeRecord.endWorkTime,
            timeWorked = timeRecord.timeWorked
        )
    }

    fun Company.convertToDto(): CompanyGetDto {
        return CompanyGetDto(
            nameCompany = this.nameCompany
        )
    }

    fun deleteEmployee(employeeRepository: EmployeeRepository, name: String, surname: String) {
        val employeeToDelete = employeeRepository.findByNameAndSurnameIgnoreCase(name, surname)
            ?: throw Exception("Employee not found with name: $name and surname: $surname")
        employeeToDelete.id?.let { employeeRepository.deleteById(it) }
    }
}
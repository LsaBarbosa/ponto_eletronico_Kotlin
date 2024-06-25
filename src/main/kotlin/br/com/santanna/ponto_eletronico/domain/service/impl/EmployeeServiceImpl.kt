package br.com.santanna.ponto_eletronico.domain.service.impl

import br.com.santanna.ponto_eletronico.domain.entity.Company
import br.com.santanna.ponto_eletronico.domain.entity.Employee
import br.com.santanna.ponto_eletronico.domain.entity.TimeRecord
import br.com.santanna.ponto_eletronico.domain.dto.company.CompanyGetDto
import br.com.santanna.ponto_eletronico.domain.dto.employee.EmployeeDto
import br.com.santanna.ponto_eletronico.domain.dto.employee.EmployeeGetDto
import br.com.santanna.ponto_eletronico.domain.dto.timeRecord.TimeRecordDto
import br.com.santanna.ponto_eletronico.infrastructure.repository.CompanyRepository
import br.com.santanna.ponto_eletronico.domain.dataprovider.EmployeeDataProvider
import br.com.santanna.ponto_eletronico.domain.service.EmployeeService
import org.springframework.stereotype.Service


@Service
class EmployeeServiceImpl(private val employeeDataProvider: EmployeeDataProvider, private val companyRepository: CompanyRepository):
    EmployeeService {
    override fun getAllEmployees(): List<EmployeeGetDto> {
        val employees = employeeDataProvider.findAll()
        return employees.map {  convertToGetEmployeeDto(it) }
    }

    override fun getEmployeeById(id: Long): EmployeeGetDto? {
        val employee = employeeDataProvider.findById(id)
        return  convertToGetEmployeeDto(employee)
    }

    override fun getEmployeeByNameAndSurname(name: String, surname: String): EmployeeGetDto? {
        val employee = employeeDataProvider.findByNameAndSurnameIgnoreCase(name,surname )
        return  convertToGetEmployeeDto(employee)
    }

    override fun registerEmployee(employeeDto: EmployeeDto): EmployeeDto {
       employeeDataProvider.existsByNameAndSurnameIgnoreCase(employeeDto.name,employeeDto.surname)
        val company = companyRepository.findByNameCompanyContainsIgnoreCase(employeeDto.companyName)

        val employeeEntity = Employee(
            name = employeeDto.name,
            surname = employeeDto.surname,
            salary = employeeDto.salary,
            position = employeeDto.position,
            company = company
        )
        val savedEmployeeEntity = employeeDataProvider.save(employeeEntity)
        return  convertToDto(savedEmployeeEntity)
    }

    override fun updateEmployee(employeeDto: EmployeeDto): EmployeeDto {
        val existingEmployeeEntity =
            employeeDataProvider.findByNameAndSurnameIgnoreCase(employeeDto.name, employeeDto.surname)

        existingEmployeeEntity?.salary = employeeDto.salary ?: existingEmployeeEntity?.salary
        existingEmployeeEntity?.password = employeeDto.password ?: existingEmployeeEntity?.password
        existingEmployeeEntity?.position = employeeDto.position ?: existingEmployeeEntity?.position

        val updatedEmployeeEntity = employeeDataProvider.save(existingEmployeeEntity!!)
        return convertToDto(updatedEmployeeEntity)
    }

    override fun deleteEmployee(  name: String, surname: String) {
        employeeDataProvider.deleteByNameAndSurname(name,surname)
    }

    fun convertToDto(employee: Employee?): EmployeeDto {
        return EmployeeDto(
            id = employee?.id,
            name = employee?.name,
            surname = employee?.surname,
            position = employee?.position,
            salary = employee?.salary,
            companyName = employee?.company?.nameCompany
        )
    }

    fun convertToGetEmployeeDto(employee: Employee?): EmployeeGetDto {
        val timeWorkedDtos = employee?.timeWorked?.map { convertToTimeRecordDto(it!!) }

        return EmployeeGetDto(
            id = employee?.id,
            name = employee?.name,
            surname = employee?.surname,
            salary = employee?.salary,
            position = employee?.position,
            password = employee?.password,
            timeWorked = timeWorkedDtos,
            company = employee?.company?.convertToDto()
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

}

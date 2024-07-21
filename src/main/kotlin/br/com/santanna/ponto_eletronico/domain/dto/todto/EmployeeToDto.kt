package br.com.santanna.ponto_eletronico.domain.dto.todto

import br.com.santanna.ponto_eletronico.domain.dto.company.CompanyGetDto
import br.com.santanna.ponto_eletronico.domain.dto.employee.EmployeeDto
import br.com.santanna.ponto_eletronico.domain.dto.employee.EmployeeGetDto
import br.com.santanna.ponto_eletronico.domain.dto.employee.UpdateEmployeeDto
import br.com.santanna.ponto_eletronico.domain.dto.timeRecord.TimeRecordDto
import br.com.santanna.ponto_eletronico.domain.entity.Company
import br.com.santanna.ponto_eletronico.domain.entity.Employee
import br.com.santanna.ponto_eletronico.domain.entity.TimeRecord
import org.springframework.stereotype.Component

@Component
class EmployeeToDto {

    fun convertToDto(employee: Employee?): EmployeeDto {
        return EmployeeDto(
            id = employee?.id,
            cpf = employee?.cpf,
            role = employee?.role,
            name = employee?.name,
            email = employee?.email,
            surname = employee?.surname,
            position = employee?.position,
            salary = employee?.salary,
            companyCNPJ = employee?.company?.companyCNPJ
        )
    }

    fun convertToUpdateEmployeeDto(employee: Employee?): UpdateEmployeeDto {
        return UpdateEmployeeDto(
            name = employee?.name,
            surname = employee?.surname,
            salary = employee?.salary,
            email = employee?.email,
            position = employee?.position,
            role = employee?.role
        )
    }

    fun convertToGetEmployeeDto(employee: Employee?): EmployeeGetDto {
        employee?.timeWorked?.map { convertToTimeRecordDto(it!!) }

        return EmployeeGetDto(
            name = employee?.name,
            surname = employee?.surname,
            salary = employee?.salary,
            position = employee?.position,
            cpf = employee?.cpf,
            role = employee?.role,
            email = employee?.email,
            company = employee?.company?.convertToDto()
        )
    }

    private fun convertToTimeRecordDto(timeRecord: TimeRecord): TimeRecordDto {
        return TimeRecordDto(
            id = timeRecord.id,
            startWorkTime = timeRecord.startWorkTime,
            endWorkTime = timeRecord.endWorkTime,
            timeWorked = timeRecord.timeWorked
        )
    }

    private fun Company.convertToDto(): CompanyGetDto {
        return CompanyGetDto(
            nameCompany = this.nameCompany
        )
    }
}

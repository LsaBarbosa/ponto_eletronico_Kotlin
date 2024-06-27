package br.com.santanna.infrastructure.impl

import br.com.santanna.domain.dataprovider.EmployeeDataProvider
import br.com.santanna.domain.entity.Employee
import br.com.santanna.infrastructure.exception.model.ObjectNotFoundException
import br.com.santanna.infrastructure.model.toEmployee
import br.com.santanna.infrastructure.model.toEmployeeModel
import br.com.santanna.infrastructure.repository.EmployeeRepository
import org.springframework.stereotype.Service

@Service
class EmployeeDataProviderImpl(val employeeRepository: EmployeeRepository) : EmployeeDataProvider {

    override fun existsByNameAndSurnameIgnoreCase(name: String?, surname: String?): Boolean {
        val employee = employeeRepository.existsByNameAndSurnameIgnoreCase(name, surname)
        if (employee) {
            throw ObjectNotFoundException("Colaborador com nome: $name $surname já existe no sistema.")
        }
        return employee
    }

    override fun findByNameAndSurnameIgnoreCase(name: String?, surname: String?): Employee {
        val employee = employeeRepository.findByNameAndSurnameIgnoreCase(name, surname)
            ?: throw ObjectNotFoundException("Colaborador com nome: $name $surname não encontrado")
        return employee.toEmployee()
    }

    override fun deleteByNameAndSurname(name: String?, surname: String?) {
        val employeToDelete = findByNameAndSurnameIgnoreCase(name, surname)
        employeeRepository.delete(employeToDelete.toEmployeeModel())
    }

    override fun findAll(): List<Employee> {
        val employees = employeeRepository.findAll()
        return employees.map { it.toEmployee() }
    }

    override fun save(employee: Employee): Employee {
        val saveEmployee =  employeeRepository.save(employee.toEmployeeModel())
        return saveEmployee.toEmployee()
    }

    override fun findById(id: Long): Employee {
        val employee = employeeRepository.findById(id)
            .orElseThrow { ObjectNotFoundException("Colaborador não encontrado") }
        return employee.toEmployee()
    }


}
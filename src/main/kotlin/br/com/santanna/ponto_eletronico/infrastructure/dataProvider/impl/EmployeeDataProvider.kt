package br.com.santanna.ponto_eletronico.infrastructure.dataProvider.impl

import br.com.santanna.ponto_eletronico.domain.entity.Employee
import br.com.santanna.ponto_eletronico.infrastructure.repository.EmployeeRepository
import br.com.santanna.ponto_eletronico.domain.dataprovider.EmployeeDataProvider
import br.com.santanna.ponto_eletronico.app.handler.model.ObjectNotFoundException
import org.springframework.stereotype.Service

@Service
class EmployeeDataProvider(val employeeRepository: EmployeeRepository) : EmployeeDataProvider {

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
        return employee
    }

    override fun deleteByNameAndSurname(name: String?, surname: String?) {
        val employeToDelete = findByNameAndSurnameIgnoreCase(name, surname)
        employeeRepository.delete(employeToDelete)
    }

    override fun findAll(): MutableList<Employee> {
        val employees = employeeRepository.findAll()
        return employees
    }

    override fun save(employee: Employee): Employee {
        return employeeRepository.save(employee)
    }

    override fun findById(id: Long): Employee {
        val employee = employeeRepository.findById(id)
            .orElseThrow { ObjectNotFoundException("Colaborador não encontrado") }
        return employee
    }


}
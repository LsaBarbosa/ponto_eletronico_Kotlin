package br.com.santanna.ponto_eletronico.infrastructure.dataProvider.impl

import br.com.santanna.ponto_eletronico.domain.entity.Employee
import br.com.santanna.ponto_eletronico.infrastructure.repository.EmployeeRepository
import br.com.santanna.ponto_eletronico.domain.dataprovider.EmployeeDataProvider
import br.com.santanna.ponto_eletronico.app.handler.model.ObjectNotFoundException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class EmployeeDataProviderImpl(val employeeRepository: EmployeeRepository) : EmployeeDataProvider {



    override fun findAll(): List<Employee> {
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

    override fun findByCpf(cpf: String): UserDetails? {
        return employeeRepository.findByCpf(cpf)
    }

    override fun findCpf(cpf: String?): Employee? {
        return employeeRepository.findByCpfIgnoreCase(cpf)
    }
    override fun deleteByCpf(cpf: String) {
        val employeeToDelete = findCpf(cpf)
            ?: throw ObjectNotFoundException("Colaborador não encontrado")
        employeeRepository.delete(employeeToDelete)
    }
}

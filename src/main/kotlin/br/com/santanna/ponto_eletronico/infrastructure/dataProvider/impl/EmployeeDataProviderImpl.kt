package br.com.santanna.ponto_eletronico.infrastructure.dataProvider.impl

import br.com.santanna.ponto_eletronico.domain.entity.Employee
import br.com.santanna.ponto_eletronico.infrastructure.repository.EmployeeRepository
import br.com.santanna.ponto_eletronico.domain.dataprovider.EmployeeDataProvider
import br.com.santanna.ponto_eletronico.app.handler.model.ObjectNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class EmployeeDataProviderImpl(val employeeRepository: EmployeeRepository) : EmployeeDataProvider {



    override fun findAll(pageable: Pageable): Page<Employee> {
        return employeeRepository.findAll(pageable)
    }

    override fun save(employee: Employee): Employee {
        return employeeRepository.save(employee)
    }

    override fun findById(id: Long): Employee {
        val employee = employeeRepository.findById(id)
            .orElseThrow { ObjectNotFoundException("Colaborador não encontrado") }
        return employee
    }
    override fun findByNameAndSurnameIgnoreCase(name: String?, surname: String?): Employee {
        val employee = employeeRepository.findByNameAndSurnameIgnoreCase(name, surname)
            ?: throw ObjectNotFoundException("Colaborador com nome: $name $surname não encontrado")
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

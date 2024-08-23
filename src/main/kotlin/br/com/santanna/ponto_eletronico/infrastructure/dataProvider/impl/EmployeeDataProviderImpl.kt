package br.com.santanna.ponto_eletronico.infrastructure.dataProvider.impl

import br.com.santanna.ponto_eletronico.app.handler.model.NotFoundException
import br.com.santanna.ponto_eletronico.domain.dataprovider.EmployeeDataProvider
import br.com.santanna.ponto_eletronico.domain.entity.employee.Employee
import br.com.santanna.ponto_eletronico.infrastructure.repository.EmployeeRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class EmployeeDataProviderImpl(val employeeRepository: EmployeeRepository) : EmployeeDataProvider {



    override fun findByCompany(companyId: UUID, pageable: Pageable): Page<Employee> {
        return employeeRepository.findByCompanyId(companyId, pageable)
    }

    override fun save(employee: Employee): Employee {
        return employeeRepository.save(employee)
    }

    override fun findById(id: UUID): Employee {
        val employee = employeeRepository.findById(id)
            .orElseThrow { NotFoundException("Colaborador não encontrado") }
        return employee
    }

    override fun findCpf(cpf: String?): Employee? {
        return employeeRepository.findByCpfIgnoreCase(cpf)
    }

    override fun deleteById(id: UUID) {
        val employeeToDelete = findById(id)

        employeeRepository.delete(employeeToDelete)
    }
}

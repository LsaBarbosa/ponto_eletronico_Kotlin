package br.com.santanna.infrastructure.repository

import br.com.santanna.infrastructure.model.EmployeeModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmployeeRepository: JpaRepository<EmployeeModel, Long> {

    fun existsByNameAndSurnameIgnoreCase(name: String?, surname: String?): Boolean
    fun findByNameAndSurnameIgnoreCase(name: String?, surname: String?): EmployeeModel?


}

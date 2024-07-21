package br.com.santanna.ponto_eletronico.infrastructure.repository

import br.com.santanna.ponto_eletronico.domain.entity.Image
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

@Repository
interface ImageRepository: JpaRepository<Image, Long> {
    fun findByIdAndEmployeeId(imageId: Long, employeeId: UUID): Image?
    @Query("SELECT i FROM Image i WHERE i.employee.id = :employeeId AND i.uploadDate BETWEEN :startDate AND :endDate")
    fun findAllByEmployeeIdAndDateRange(
        @Param("employeeId") employeeId: UUID,
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): List<Image>
    @Query("SELECT i FROM Image i WHERE i.employee.cpf = :cpf AND i.uploadDate BETWEEN :startDate AND :endDate")
    fun findAllByEmployeeCpfAndDateRange(cpf: String, startDate: LocalDate, endDate: LocalDate): List<Image>
    fun findByIdAndEmployeeCpf(id: Long, cpf: String): Image?

}
package br.com.santanna.ponto_eletronico.infrastructure.repository

import br.com.santanna.ponto_eletronico.domain.entity.Image
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface ImageRepository: JpaRepository<Image, Long> {
    fun findByEmployeeCpf(cpf: String): Image?
    fun findAllByEmployeeCpf(cpf: String): List<Image>
    fun findByIdAndEmployeeCpf(id: Long, cpf: String): Image?

    @Query("SELECT i FROM Image i WHERE i.employee.cpf = :cpf AND i.uploadDate BETWEEN :startDate AND :endDate")
    fun findAllByEmployeeCpfAndDateRange(cpf: String, startDate: LocalDate, endDate: LocalDate): List<Image>
}
package br.com.santanna.ponto_eletronico.infrastructure.repository

import br.com.santanna.ponto_eletronico.domain.entity.Image
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ImageRepository: JpaRepository<Image, Long> {
    fun findByEmployeeCpf(cpf: String): Image?
    fun findAllByEmployeeCpf(cpf: String): List<Image>
    fun findByIdAndEmployeeCpf(id: Long, cpf: String): Image?
}
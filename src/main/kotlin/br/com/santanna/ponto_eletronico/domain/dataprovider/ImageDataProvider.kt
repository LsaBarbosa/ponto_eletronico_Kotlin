package br.com.santanna.ponto_eletronico.domain.dataprovider

import br.com.santanna.ponto_eletronico.domain.entity.Image
import java.time.LocalDate
import java.util.*

interface ImageDataProvider {
    fun save(image: Image): Image
    fun findByIdAndEmployeeId(imageId: Long, employeeId: UUID): Image?
    fun delete(image: Image)
    fun findAllByEmployeeIdAndDateRange(employeeId: UUID, startDate: LocalDate, endDate: LocalDate): List<Image>
    fun findAllByEmployeeCpfAndDateRange(cpf: String, startDate: LocalDate, endDate: LocalDate): List<Image>
    fun findByIdAndEmployeeCpf(imageId: Long?, employeeCpf: String): Image?
}


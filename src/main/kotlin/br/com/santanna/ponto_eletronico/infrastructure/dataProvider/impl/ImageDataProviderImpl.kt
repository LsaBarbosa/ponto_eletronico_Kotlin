package br.com.santanna.ponto_eletronico.infrastructure.dataProvider.impl

import br.com.santanna.ponto_eletronico.app.handler.model.DataIntegrityViolationException
import br.com.santanna.ponto_eletronico.domain.dataprovider.ImageDataProvider
import br.com.santanna.ponto_eletronico.domain.entity.Image
import br.com.santanna.ponto_eletronico.infrastructure.repository.ImageRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class ImageDataProviderImpl(
    private val imageRepository: ImageRepository
) : ImageDataProvider {

    override fun save(image: Image): Image {
        try {

        return imageRepository.save(image)
        }catch (ex:Exception){
            throw DataIntegrityViolationException("Erro no processamento dos dados, image:$image")
        }

    }

    override fun findByIdAndEmployeeId(imageId: Long, employeeId: UUID): Image? {
        try {
            return imageRepository.findByIdAndEmployeeId(imageId, employeeId)
        } catch (ex: Exception) {
            throw DataIntegrityViolationException("Erro no processamento dos dados, employeeId:$employeeId, imageId:$imageId")
        }
    }

    override fun findAllByEmployeeIdAndDateRange(
        employeeId: UUID, startDate: LocalDate, endDate: LocalDate
    ): List<Image> {
        try {
            return imageRepository.findAllByEmployeeIdAndDateRange(employeeId, startDate, endDate)
        } catch (ex: Exception) {
            throw DataIntegrityViolationException("Erro no processamento dos dados, employeeId:$employeeId, startDate:$startDate,endDate:$endDate")

        }
    }

    override fun findAllByEmployeeCpfAndDateRange(cpf: String, startDate: LocalDate, endDate: LocalDate): List<Image> {
        try {
            return imageRepository.findAllByEmployeeCpfAndDateRange(cpf, startDate, endDate)
        } catch (ex: Exception) {
            throw DataIntegrityViolationException("Erro no processamento dos dados, CPF:$cpf, startDate$startDate, endDate$endDate")
        }
    }

    override fun findByIdAndEmployeeCpf(imageId: Long?, employeeCpf: String): Image? {
        return try {
            return imageId?.let { imageRepository.findByIdAndEmployeeCpf(it, employeeCpf) }
        } catch (ex: Exception) {
            throw DataIntegrityViolationException("Erro no processamento dos dados, imageId:$imageId, employeeCpf:$employeeCpf")
        }

    }

    override fun delete(image: Image) {
        imageRepository.delete(image)
    }
}

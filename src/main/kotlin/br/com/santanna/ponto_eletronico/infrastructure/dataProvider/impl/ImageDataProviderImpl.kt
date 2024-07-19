package br.com.santanna.ponto_eletronico.infrastructure.dataProvider.impl

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
        return imageRepository.save(image)
    }


    override fun findByIdAndEmployeeId(imageId: Long, employeeId: UUID): Image? {
        return imageRepository.findByIdAndEmployeeId(imageId, employeeId)
    }

    override fun findAllByEmployeeIdAndDateRange(
        employeeId: UUID,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<Image> {
        return imageRepository.findAllByEmployeeIdAndDateRange(employeeId, startDate, endDate)
    }

    override fun findAllByEmployeeCpfAndDateRange(cpf: String, startDate: LocalDate, endDate: LocalDate): List<Image> {
        return imageRepository.findAllByEmployeeCpfAndDateRange(cpf, startDate, endDate)
    }

    override fun findByIdAndEmployeeCpf(imageId: Long?, employeeCpf: String): Image? {
        return imageId?.let { imageRepository.findByIdAndEmployeeCpf(it, employeeCpf) }
    }

    override fun delete(image: Image) {
        imageRepository.delete(image)
    }
}

//
//    override fun findAllByEmployeeCpf(cpf: String): List<Image> {
//        return imageRepository.findAllByEmployeeCpf(cpf)
//    }
//
//    override fun findByIdAndEmployeeCpf(imageId: Long?, employeeCpf: String): Image? {
//        return imageId?.let { imageRepository.findByIdAndEmployeeCpf(it, employeeCpf) }
//    }
//    override fun findAllByEmployeeId(employeeId: UUID): List<Image> {
//        return imageRepository.findAllByEmployeeId(employeeId)
//    }
//
//
//}
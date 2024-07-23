package br.com.santanna.ponto_eletronico.domain.dto.image.manager

import br.com.santanna.ponto_eletronico.domain.dto.image.update.UpdateImageMessageDto
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.br.CPF

data class ManagerImageRequestDto(
    @field:NotBlank(message = "O CPF do funcionário não pode estar em branco")
    @field:CPF
    @field:Size(min = 11, max = 11, message = "O CPF do funcionário deve ter exatamente 11 caracteres")
    val employeeCpf: String,
    val imageId: Long?,
    val updateImageMessageDto: UpdateImageMessageDto?
)

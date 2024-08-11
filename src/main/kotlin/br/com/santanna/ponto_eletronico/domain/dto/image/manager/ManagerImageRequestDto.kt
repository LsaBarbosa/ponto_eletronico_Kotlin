package br.com.santanna.ponto_eletronico.domain.dto.image.manager

import br.com.santanna.ponto_eletronico.domain.dto.image.update.UpdateImageMessageDto
import java.util.*

data class ManagerImageRequestDto(
    val employeeId: UUID,
    val imageId: Long?,
    val updateImageMessageDto: UpdateImageMessageDto?
)

package br.com.santanna.ponto_eletronico.app.entrypoint.http

import br.com.santanna.ponto_eletronico.domain.dto.image.manager.ManagerImageRequestDto
import br.com.santanna.ponto_eletronico.domain.dto.image.search.ImageListDto
import br.com.santanna.ponto_eletronico.domain.dto.image.search.ImageSearchDto
import br.com.santanna.ponto_eletronico.domain.dto.image.update.UpdateImageMessageDto
import br.com.santanna.ponto_eletronico.domain.dto.image.update.UploadImageRequestDto
import br.com.santanna.ponto_eletronico.domain.service.ImageService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/files")
@Tag(name = "Arquivos", description = "End-point para gestão de arquivos")
@SecurityRequirement(name = "Bearer Authentication")
class ImageController(private val imageService: ImageService) {

    @PostMapping("/upload")
    @Operation(summary = "Upload de imagem")
    fun uploadImage(

        @RequestPart("file") file: MultipartFile,
        @RequestPart("message", required = false) message: String?
    ): ResponseEntity<Void> {
        val uploadImageRequestDto = UploadImageRequestDto(
            file = file,
            message = message
        )
        imageService.storeImageForCurrentUser(uploadImageRequestDto)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/search/all")
    @Operation(summary = "Lista todas as imagens do funcionário logado")
    fun getImages(@RequestBody imageSearchDto: ImageSearchDto): ResponseEntity<List<ImageListDto>> {
        val images = imageService.getImagesForCurrentUser(imageSearchDto)
        return ResponseEntity.ok(images)
    }

    @GetMapping("/download/{imageId}")
    @Operation(summary = "Download de imagem")
    fun downloadImage(@PathVariable imageId: Long): ResponseEntity<ByteArrayResource> {
        val imageData = imageService.getImageByIdForCurrentUser(imageId)
        val resource = ByteArrayResource(imageData)

        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$imageId-image.jpg\"")
            .body(resource)
    }

    @PatchMapping("/{imageId}")
    @Operation(summary = "Altera a descrição da imagem")
    fun updateImageMessage(
        @PathVariable imageId: Long,
        @Valid @RequestBody updateImageMessageDto: UpdateImageMessageDto
    ): ResponseEntity<ImageListDto> {
        val imageDto = imageService.updateImageMessageForCurrentUser(imageId, updateImageMessageDto)
        return ResponseEntity.ok(imageDto)
    }

    @DeleteMapping("/{imageId}")
    @Operation(summary = "Deleta imagem por ID")
    fun deleteImageById(@PathVariable imageId: Long): ResponseEntity<Void> {
        imageService.deleteImageByIdForCurrentUser(imageId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("search/adm/imagens/{cpf}")
    @Operation(summary = "Busca todas as imagens do funcionário pelo manager")
    fun getImagesByEmployeeCpfAsManager(
        @PathVariable cpf: String,
        @RequestBody imageSearchDto: ImageSearchDto
    ): ResponseEntity<List<ImageListDto>> {
        val managerImageRequestDto =
            ManagerImageRequestDto(employeeCpf = cpf, imageId = null, updateImageMessageDto = null)
        val images = imageService.getImagesByEmployeeCpfAsManager(managerImageRequestDto, imageSearchDto)
        return ResponseEntity.ok(images)
    }

    @GetMapping("/adm/download/{imageId}")
    @Operation(summary = "Download de imagem pelo manager")
    fun downloadImageAsManager(@PathVariable imageId: Long, @RequestParam("cpf") cpf: String): ResponseEntity<ByteArrayResource> {
        val managerImageRequestDto = ManagerImageRequestDto(employeeCpf = cpf, imageId = imageId, updateImageMessageDto = null)
        val imageData = imageService.getImageByIdAsManager(managerImageRequestDto)
        val resource = ByteArrayResource(imageData)

        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$imageId-image.jpg\"")
            .body(resource)
    }

    @PatchMapping("/adm/delete/{imageId}")
    @Operation(summary = "Altera a descrição da imagem pelo manager")
    fun updateImageMessageAsManager(
        @PathVariable imageId: Long,
        @Valid @RequestBody updateImageMessageDto: UpdateImageMessageDto,
        @RequestParam("cpf") cpf: String
    ): ResponseEntity<ImageListDto> {
        val managerImageRequestDto =
            ManagerImageRequestDto(employeeCpf = cpf, imageId = imageId, updateImageMessageDto = updateImageMessageDto)
        val imageDto = imageService.updateImageMessageAsManager(managerImageRequestDto)
        return ResponseEntity.ok(imageDto)
    }



    @DeleteMapping("/adm/delete/{imageId}")
    @Operation(summary = "Deleta imagem por ID pelo manager")
    fun deleteImageByIdAsManager(@PathVariable imageId: Long, @RequestParam("cpf") cpf: String): ResponseEntity<Void> {
        val managerImageRequestDto =
            ManagerImageRequestDto(employeeCpf = cpf, imageId = imageId, updateImageMessageDto = null)
        imageService.deleteImageByIdAsManager(managerImageRequestDto)
        return ResponseEntity.noContent().build()
    }


}
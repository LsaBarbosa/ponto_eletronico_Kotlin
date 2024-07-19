package br.com.santanna.ponto_eletronico.app.entrypoint.http

import br.com.santanna.ponto_eletronico.domain.dto.image.*
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
@RequestMapping("/api/images")
@Tag(name = "Imagem", description = "End-point para gestão das imagens")
@SecurityRequirement(name = "Bearer Authentication")
class ImageController(private val imageService: ImageService) {

    @PostMapping("/upload")
    @Operation(summary = "Upload de imagem")
    fun uploadImage(  @RequestPart("cpf") cpf: String,
                      @RequestPart("passwords") passwords: String,
                      @RequestPart("file") file: MultipartFile,
                      @RequestPart("message", required = false) message: String?): ResponseEntity<Void> {
        val uploadImageRequestDto = UploadImageRequestDto(
            cpf = cpf,
            passwords = passwords,
            file = file,
            message = message
        )
        imageService.storeImage(uploadImageRequestDto)
        return ResponseEntity.ok().build()
    }


    @GetMapping("/download")
    @Operation(summary = "Download de imagem")
    fun downloadImage(@RequestParam("cpf") cpf: String): ResponseEntity<ByteArrayResource> {
        val imageData = imageService.getImageByEmployeeCpf(cpf)
        val resource = ByteArrayResource(imageData)

        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$cpf-image.jpg\"")
            .body(resource)
    }

    @GetMapping
    @Operation(summary = "Busca imagem por id")
    fun searchImages(@Valid @RequestBody imageSearchDto: ImageSearchDto): ResponseEntity<List<ImageListDto>> {
        val images = imageService.getImagesByEmployeeCpfAndDateRange(imageSearchDto)
        return ResponseEntity.ok(images)
    }

    @PatchMapping
    @Operation(summary = "Altera a descrição da imagem")
    fun updateImageMessage(@Valid @RequestBody updateImageRequestDto: UpdateImageRequestDto): ResponseEntity<ImageListDto> {
        val imageDto = imageService.updateImageMessage(updateImageRequestDto)
        return ResponseEntity.ok(imageDto)
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta imagem por id")
    fun deleteImageById(@RequestBody deleteImageRequestDto: DeleteImageRequestDto): ResponseEntity<Void> {
        imageService.deleteImageById(deleteImageRequestDto)
        return ResponseEntity.noContent().build()
    }
}
package br.com.santanna.ponto_eletronico.app.entrypoint.http

import br.com.santanna.ponto_eletronico.domain.dto.image.DeleteImageRequestDto
import br.com.santanna.ponto_eletronico.domain.dto.image.ImageListDto
import br.com.santanna.ponto_eletronico.domain.dto.image.UpdateImageMessageDto
import br.com.santanna.ponto_eletronico.domain.dto.image.UploadImageRequestDto
import br.com.santanna.ponto_eletronico.domain.service.ImageService
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/images")
class ImageController(private val imageService: ImageService) {

    @PostMapping("/upload")
    fun uploadImage(@RequestParam("cpf") cpf: String, @RequestParam("file") file: MultipartFile, @RequestParam("message", required = false) message: String?, @RequestParam("passwords") passwords: String): ResponseEntity<Void> {
        val uploadImageRequestDto = UploadImageRequestDto(cpf, file, message, passwords)
        imageService.storeImage(uploadImageRequestDto)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/download")
    fun downloadImage(@RequestParam("cpf") cpf: String): ResponseEntity<ByteArrayResource> {
        val imageData = imageService.getImageByEmployeeCpf(cpf)
        val resource = ByteArrayResource(imageData)

        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$cpf-image.jpg\"")
            .body(resource)
    }

    @GetMapping
    fun getImagesByEmployeeCpf(@RequestParam("cpf") cpf: String): ResponseEntity<List<ImageListDto>> {
        val images = imageService.getImagesByEmployeeCpf(cpf)
        return ResponseEntity.ok(images)
    }

    @GetMapping("/download/{id}")
    fun downloadImageById(@PathVariable id: Long): ResponseEntity<ByteArrayResource> {
        val imageData = imageService.getImageById(id)
        val resource = ByteArrayResource(imageData)

        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"image-$id.jpg\"")
            .body(resource)
    }
    @PatchMapping("/{id}")
    fun updateImageMessage(
        @PathVariable id: Long,
        @RequestParam("cpf") cpf: String,
        @RequestBody updateImageMessageDto: UpdateImageMessageDto
    ): ResponseEntity<ImageListDto> {
        val imageDto = imageService.updateImageMessage(id, cpf, updateImageMessageDto)
        return ResponseEntity.ok(imageDto)
    }

    @DeleteMapping("/{id}")
    fun deleteImageById(
        @PathVariable id: Long,
        @RequestBody deleteImageRequestDto: DeleteImageRequestDto
    ): ResponseEntity<Void> {
        imageService.deleteImageById(id, deleteImageRequestDto.cpf, deleteImageRequestDto.passwords)
        return ResponseEntity.noContent().build()
    }
}
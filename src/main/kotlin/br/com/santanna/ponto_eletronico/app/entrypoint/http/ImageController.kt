package br.com.santanna.ponto_eletronico.app.entrypoint.http

import br.com.santanna.ponto_eletronico.domain.dto.image.*
import br.com.santanna.ponto_eletronico.domain.service.ImageService
import jakarta.validation.Valid
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
    fun downloadImage(@RequestParam("cpf") cpf: String): ResponseEntity<ByteArrayResource> {
        val imageData = imageService.getImageByEmployeeCpf(cpf)
        val resource = ByteArrayResource(imageData)

        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$cpf-image.jpg\"")
            .body(resource)
    }

    @GetMapping
    fun searchImages(@Valid @RequestBody imageSearchDto: ImageSearchDto): ResponseEntity<List<ImageListDto>> {
        val images = imageService.getImagesByEmployeeCpfAndDateRange(imageSearchDto)
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
    @PatchMapping
    fun updateImageMessage(@Valid @RequestBody updateImageRequestDto: UpdateImageRequestDto): ResponseEntity<ImageListDto> {
        val imageDto = imageService.updateImageMessage(updateImageRequestDto)
        return ResponseEntity.ok(imageDto)
    }

    @DeleteMapping("/{id}")
    fun deleteImageById(@RequestBody deleteImageRequestDto: DeleteImageRequestDto): ResponseEntity<Void> {
        imageService.deleteImageById(deleteImageRequestDto)
        return ResponseEntity.noContent().build()
    }
}
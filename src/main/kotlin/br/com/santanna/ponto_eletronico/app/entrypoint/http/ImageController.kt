package br.com.santanna.ponto_eletronico.app.entrypoint.http

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

    fun uploadImage(@RequestParam("cpf") cpf: String, @RequestParam("file") file: MultipartFile): ResponseEntity<Void> {
        imageService.storeImage(cpf, file)
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
}
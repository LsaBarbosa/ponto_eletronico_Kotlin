package br.com.santanna.ponto_eletronico.app.handler

import br.com.santanna.ponto_eletronico.app.handler.model.DataIntegrityViolationException
import br.com.santanna.ponto_eletronico.app.handler.model.ObjectNotFoundException
import br.com.santanna.ponto_eletronico.app.handler.model.StandardError
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.LocalDateTime

@ControllerAdvice
class ResourceExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(ex: Exception, request: HttpServletRequest): ResponseEntity<StandardError> {
        val status = HttpStatus.INTERNAL_SERVER_ERROR
        val error = "Internal server error"
        val timestamp = LocalDateTime.now()
        val path = request.requestURI

        val errorResponse = StandardError(timestamp, status.value(), error, path)

        return ResponseEntity.status(status).body(errorResponse)
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolationException(ex: DataIntegrityViolationException, request: HttpServletRequest): ResponseEntity<StandardError> {
        val status = HttpStatus.BAD_REQUEST
        val error = "Data integrity violation"
        val timestamp = LocalDateTime.now()
        val path = request.requestURI

        val errorResponse = StandardError(timestamp, status.value(), error, path)

        return ResponseEntity.status(status).body(errorResponse)
    }

    @ExceptionHandler(ObjectNotFoundException::class)
    fun handleObjectNotFoundException(ex: ObjectNotFoundException, request: HttpServletRequest): ResponseEntity<StandardError> {
        val status = HttpStatus.NOT_FOUND
        val error = ex.message // Use the message from your custom exception
        val timestamp = LocalDateTime.now()
        val path = request.requestURI

        val errorResponse = StandardError(timestamp, status.value(), error, path)

        return ResponseEntity.status(status).body(errorResponse)
    }

}
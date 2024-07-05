package br.com.santanna.ponto_eletronico.app.handler

import br.com.santanna.ponto_eletronico.app.handler.model.DataIntegrityViolationException
import br.com.santanna.ponto_eletronico.app.handler.model.ObjectNotFoundException
import br.com.santanna.ponto_eletronico.app.handler.model.StandardError
import br.com.santanna.ponto_eletronico.app.handler.model.ValidationError
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
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

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException, request: HttpServletRequest): ResponseEntity<ValidationError> {
        val errors: MutableMap<String, String?> = HashMap()
        ex.bindingResult.allErrors.forEach { error ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.getDefaultMessage()
            errors[fieldName] = errorMessage
        }

        val errorResponse = ValidationError(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Validation failed",
            path = request.requestURI,
            validationErrors = errors
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }
}
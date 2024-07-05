package br.com.santanna.ponto_eletronico.app.handler.model

import java.time.LocalDateTime

data class ValidationError(val timestamp: LocalDateTime? = null,
                           val status: Int? = null,
                           val error: String? = null,
                           val path: String? = null,
                           val validationErrors: Map<String, String?>? = null)

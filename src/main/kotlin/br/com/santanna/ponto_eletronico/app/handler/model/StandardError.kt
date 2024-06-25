package br.com.santanna.ponto_eletronico.app.handler.model

import java.time.LocalDateTime



data class StandardError (
     val timestamp: LocalDateTime? = null,
     val status: Int?=null,
     val error: String? = null,
     val path: String? = null
)
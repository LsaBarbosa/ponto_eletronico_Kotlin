package br.com.santanna.ponto_eletronico.infrastructure.config

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object   DateUtils {
    private val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    private val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    fun formatDate(date: LocalDate): String = DATE_FORMATTER.format(date)

    fun parseDate(dateStr: String): LocalDate = LocalDate.parse(dateStr, DATE_FORMATTER)

    fun formatTime(dateTime: LocalDateTime): String = TIME_FORMATTER.format(dateTime)
}
package br.com.santanna.ponto_eletronico.infrastructure.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateUtils {
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    fun formatDate(date: LocalDate?): String? {
        return date?.format(dateFormatter)
    }

    fun formatTime(dateTime: LocalDateTime?): String? {
        return dateTime?.format(timeFormatter)
    }

    fun parseDate(date: String): LocalDate {
        return LocalDate.parse(date, dateFormatter)
    }

    fun parseDateTime(dateTime: String, time: String): LocalDateTime {
        return LocalDateTime.parse("$dateTime $time", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
    }
}
package com.example.vkr.presentation.components

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateTimeUtils {
    val DISPLAY_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy")
    val SERVER_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    fun parseServerFormatted(input: String): LocalDateTime? {
        return try {
            LocalDateTime.parse(input, SERVER_FORMATTER)
        } catch (e: Exception) {
            null
        }
    }

    fun parseIsoFormatted(input: String): LocalDateTime? {
        return try {
            LocalDateTime.parse(input)
        } catch (e: Exception) {
            null
        }
    }

    fun formatDisplay(datetime: LocalDateTime): String {
        return datetime.format(DISPLAY_FORMATTER)
    }
}
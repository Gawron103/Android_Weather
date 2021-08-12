package com.example.weather.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class DateProvider {
    fun getDate(): String {
        val builder = StringBuilder()
        val currentMoment: Instant = Clock.System.now()
        val dateTimeInSystemZone: kotlinx.datetime.LocalDateTime = currentMoment.toLocalDateTime(
            TimeZone.currentSystemDefault())

        builder.append(dateTimeInSystemZone.dayOfWeek.toString().lowercase().replaceFirstChar { it.uppercase() })
            .append(", ")
            .append(dateTimeInSystemZone.month.toString().lowercase().replaceFirstChar { it.uppercase() })
            .append(" ")
            .append(dateTimeInSystemZone.dayOfMonth.toString())

        return builder.toString()
    }
}
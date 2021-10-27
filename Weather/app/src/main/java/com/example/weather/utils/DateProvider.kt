package com.example.weather.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object DateProvider {

    fun getDayName(timestamp: Int): String {
        val convertedTime = Instant.fromEpochMilliseconds(timestamp.toLong() * 1000)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        return convertedTime.dayOfWeek.toString().lowercase().replaceFirstChar { it.uppercase() }
    }

    fun convertTime(timestamp: Int): String {
        val convertedTime = Instant.fromEpochMilliseconds(timestamp.toLong() * 1000)
            .toLocalDateTime(TimeZone.currentSystemDefault())

        return convertedTime.toString().split("T", ".")[1].toString()
    }

}
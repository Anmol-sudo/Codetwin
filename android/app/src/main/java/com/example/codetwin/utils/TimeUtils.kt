package com.example.codetwin.utils

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object TimeUtils {
    fun getRelativeTime(isoString: String?): String {
        if (isoString == null) return ""
        return try {
            // Backend usually sends ISO format like 2024-04-08T10:00:00
            val postTime = LocalDateTime.parse(isoString.substringBefore("."), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            val now = LocalDateTime.now()

            val seconds = ChronoUnit.SECONDS.between(postTime, now)
            val minutes = ChronoUnit.MINUTES.between(postTime, now)
            val hours = ChronoUnit.HOURS.between(postTime, now)
            val days = ChronoUnit.DAYS.between(postTime, now)

            when {
                seconds < 60 -> "Just now"
                minutes < 60 -> "$minutes m"
                hours < 24 -> "$hours h"
                days < 7 -> "$days d"
                else -> postTime.format(DateTimeFormatter.ofPattern("MMM dd"))
            }
        } catch (e: Exception) {
            isoString.substringBefore("T")
        }
    }
}
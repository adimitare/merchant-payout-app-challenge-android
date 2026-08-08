package com.example.androidinterview.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val dateFormatter =
    DateTimeFormatter.ofPattern("dd MMM yyyy")
        .withZone(ZoneId.systemDefault())

fun formatDate(date: String): String {
    return dateFormatter.format(Instant.parse(date))
}
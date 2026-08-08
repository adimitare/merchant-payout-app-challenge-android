package com.example.androidinterview.ui.transactions

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/*
fun transactionDateLabel(
    isoDate: String
): String {
    val date = Instant.parse(isoDate)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    val today = LocalDate.now()
    return when (date) {
        today -> "Today"
        today.minusDays(1) -> "Yesterday"
        else -> date.format(
            DateTimeFormatter.ofPattern(
                "dd MMM yyyy"
            )
        )
    }
}*/

package com.example.androidinterview.util

import com.example.androidinterview.domain.model.Currency

fun formatMoney(
    amount: Int,
    currency: Currency
): String {
    val symbol = when(currency) {
        Currency.GBP -> "£"
        Currency.EUR -> "€"
    }
    return "$symbol%.2f".format(
        amount / 100.0
    )
}

fun formatMoney(
    amount: String,
    currency: Currency
): String {
    return formatMoney(amount.toInt(), currency)
}
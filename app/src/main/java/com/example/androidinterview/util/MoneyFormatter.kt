package com.example.androidinterview.util

import com.example.androidinterview.domain.model.Currency
import java.text.NumberFormat
import java.util.Locale

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
    val value = amount.toBigDecimalOrNull() ?: return amount
    val formatter = NumberFormat.getCurrencyInstance(Locale.UK)
    formatter.currency = java.util.Currency.getInstance(currency.name)
    return formatter.format(value)
}
package com.example.androidinterview.domain.model

sealed class PayoutException : Exception() {
    data object InsufficientFunds : PayoutException()
    data object ServiceUnavailable : PayoutException()
    data class ApiError(
        val errorMessage: String
    ) : PayoutException()
}
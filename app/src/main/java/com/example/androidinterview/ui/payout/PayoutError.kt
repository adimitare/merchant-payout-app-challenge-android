package com.example.androidinterview.ui.payout

sealed interface PayoutError {
    data object InsufficientFunds : PayoutError
    data object ServiceUnavailable : PayoutError
    data object ApiError : PayoutError
    data object Unknown : PayoutError
}
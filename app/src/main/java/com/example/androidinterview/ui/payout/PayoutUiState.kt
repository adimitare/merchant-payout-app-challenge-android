package com.example.androidinterview.ui.payout

import com.example.androidinterview.domain.model.Currency

sealed interface PayoutUiState {
    data class Form(
        val amount: String = "",
        val currency: Currency = Currency.GBP,
        val iban: String = "",
        val amountError: String? = null,
        val ibanError: Int? = null
    ) : PayoutUiState

    data class Confirming(
        val amount: String,
        val currency: Currency,
        val iban: String
    ) : PayoutUiState

    data object Submitting : PayoutUiState

    data class Success(
        val amount: String,
        val currency: Currency
    ) : PayoutUiState

    data class Error(
        val message: String
    ) : PayoutUiState

    data class InsufficientFunds(
        val message: String = "Insufficient funds."
    ) : PayoutUiState
}
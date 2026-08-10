package com.example.androidinterview.ui.payout

import com.example.androidinterview.domain.model.Currency
import com.example.androidinterview.domain.model.PayoutResult

data class PayoutFormData(
    val amount: String = "",
    val currency: Currency = Currency.GBP,
    val iban: String = "",
    val amountError: Int? = null,
    val ibanError: Int? = null
)

sealed interface PayoutUiState {

    data class Form(
        val data: PayoutFormData = PayoutFormData()
    ) : PayoutUiState

    data class Confirming(
        val data: PayoutFormData
    ) : PayoutUiState

    data object Submitting : PayoutUiState

    data class Success(
        val payout: PayoutResult
    ) : PayoutUiState

    data class Error(
        val error: PayoutError,
        val data: PayoutFormData
    ) : PayoutUiState
}
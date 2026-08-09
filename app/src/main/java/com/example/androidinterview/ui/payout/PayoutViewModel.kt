package com.example.androidinterview.ui.payout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidinterview.domain.model.Currency
import com.example.androidinterview.domain.usecase.ValidateIbanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PayoutViewModel @Inject constructor(
    // private val repository: PayoutRepository
    private val validateIbanUseCase: ValidateIbanUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<PayoutUiState>(
            PayoutUiState.Form()
        )

    val uiState: StateFlow<PayoutUiState> =
        _uiState.asStateFlow()

    fun onAmountChanged(value: String) {
        val current = _uiState.value as? PayoutUiState.Form
            ?: return
        if (
            value.isEmpty() ||
            value.matches(Regex("""\d+(\.\d{0,2})?"""))
        ) {
            _uiState.value = current.copy(
                amount = value,
                amountError = null
            )
        }
    }

    fun onCurrencyChanged(currency: Currency) {
        val current = _uiState.value as? PayoutUiState.Form
            ?: return

        _uiState.value = current.copy(
            currency = currency
        )
    }

    fun onIbanChanged(value: String) {
        val current = _uiState.value as? PayoutUiState.Form
            ?: return

        _uiState.value = current.copy(
            iban = value,
            ibanError = null
        )
    }

    fun onConfirmClicked() {
        val current = _uiState.value as? PayoutUiState.Form
            ?: return

        val amountError = validateAmount(current.amount)
        val ibanError = validateIbanUseCase(current.iban)

        if (amountError != null || ibanError != null) {
            _uiState.value = current.copy(
                amountError = amountError,
                ibanError =  ibanError
            )
            return
        }

        _uiState.value = PayoutUiState.Confirming(
            amount = current.amount,
            currency = current.currency,
            iban = current.iban
        )
    }

    fun cancelConfirmation() {
        val current = _uiState.value as? PayoutUiState.Confirming
            ?: return

        _uiState.value = PayoutUiState.Form(
            amount = current.amount,
            currency = current.currency,
            iban = current.iban
        )
    }

    fun confirmPayout() {
        val current = _uiState.value as? PayoutUiState.Confirming
            ?: return

        viewModelScope.launch {
            _uiState.value = PayoutUiState.Submitting

            /*
             * Replace this with the repository call when
             * the payout API is wired.
             *
             * repository.createPayout(...)
             */

            // Temporary implementation for the UI flow.
            // Remove once API integration is connected.
            _uiState.value = PayoutUiState.Success(
                amount = current.amount,
                currency = current.currency
            )
        }
    }

    fun createAnotherPayout() {
        _uiState.value = PayoutUiState.Form()
    }

    fun retry() {
        _uiState.value = PayoutUiState.Form()
    }

    private fun validateAmount(
        amount: String
    ): String? {
        val value = amount.toBigDecimalOrNull()

        return when {
            amount.isBlank() ->
                "Enter an amount"

            value == null ->
                "Enter a valid amount"

            value <= java.math.BigDecimal.ZERO ->
                "Amount must be greater than zero"

            else -> null
        }
    }
}
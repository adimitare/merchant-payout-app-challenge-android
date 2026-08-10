package com.example.androidinterview.ui.payout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidinterview.R
import com.example.androidinterview.domain.model.Currency
import com.example.androidinterview.domain.model.PayoutException
import com.example.androidinterview.domain.usecase.SubmitPayoutUseCase
import com.example.androidinterview.domain.usecase.ValidateIbanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PayoutViewModel @Inject constructor(
    private val validateIbanUseCase: ValidateIbanUseCase,
    private val submitPayoutUseCase: SubmitPayoutUseCase
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
            value.matches(amountRegex)
        ) {
            _uiState.value = current.copy(
                data = current.data.copy(
                    amount = value,
                    amountError = null
                )
            )
        }
    }

    fun onCurrencyChanged(currency: Currency) {
        val current = _uiState.value as? PayoutUiState.Form
            ?: return

        _uiState.value = current.copy(
            data = current.data.copy(
                currency = currency
            )
        )
    }

    fun onIbanChanged(value: String) {
        val current = _uiState.value as? PayoutUiState.Form
            ?: return

        _uiState.value = current.copy(
            data = current.data.copy(
                iban = value,
                ibanError = null
            )
        )
    }

    fun onConfirmClicked() {
        val current = _uiState.value as? PayoutUiState.Form
            ?: return

        val data = current.data
        val amountError = validateAmount(data.amount)
        val ibanError = validateIbanUseCase(data.iban)

        if (amountError != null || ibanError != null) {
            _uiState.value = current.copy(
                data = data.copy(
                    amountError = amountError,
                    ibanError = ibanError
                )
            )
            return
        }

        _uiState.value = PayoutUiState.Confirming(
            data = data
        )
    }

    fun cancelConfirmation() {
        val current = _uiState.value as? PayoutUiState.Confirming
            ?: return

        val data = current.data
        _uiState.value = PayoutUiState.Form(
            data = data
        )
    }

    fun createAnotherPayout() {
        _uiState.value = PayoutUiState.Form()
    }

    fun retry() {
        val current = _uiState.value as? PayoutUiState.Error
            ?: return

        _uiState.value = PayoutUiState.Confirming(
            data = current.data
        )
    }

    fun submitPayout() {
        val current = _uiState.value as? PayoutUiState.Confirming
            ?: return

        val amount = parseAmountToMinorUnits(current.data.amount)
            ?: run {
                _uiState.value = PayoutUiState.Error(
                    error = PayoutError.Unknown,
                    data = current.data
                )
                return
            }
        viewModelScope.launch {
            _uiState.value = PayoutUiState.Submitting
            runCatching {
                submitPayoutUseCase(
                    amount = amount,
                    currency = current.data.currency.name,
                    iban = current.data.iban
                )
            }.onSuccess { result ->
                _uiState.value = PayoutUiState.Success(
                    payout = result
                )
            }.onFailure { throwable ->
                _uiState.value = PayoutUiState.Error(
                    error = mapPayoutError(throwable),
                    data = current.data
                )
            }
        }
    }

    private fun mapPayoutError(throwable: Throwable): PayoutError {
        return when (throwable) {
            PayoutException.InsufficientFunds ->
                PayoutError.InsufficientFunds

            PayoutException.ServiceUnavailable ->
                PayoutError.ServiceUnavailable

            is PayoutException.ApiError ->
                PayoutError.ApiError

            else ->
                PayoutError.Unknown
        }
    }

    private fun parseAmountToMinorUnits(amount: String): Int? {
        return runCatching {
            amount
                .toBigDecimalOrNull()
                ?.movePointRight(2)
                ?.intValueExact()
        }.getOrNull()
    }

    private fun validateAmount(
        amount: String
    ): Int? {
        val value = amount.toBigDecimalOrNull()
        return when {
            amount.isBlank() ->
                R.string.enter_an_amount
            value == null ->
                R.string.enter_a_valid_amount
            value <= java.math.BigDecimal.ZERO ->
                R.string.amount_must_be_greater_than_zero
            else -> null
        }
    }

    companion object {
        private val amountRegex = Regex("""\d+(\.\d{0,2})?""")
    }
}
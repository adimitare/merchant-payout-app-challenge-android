package com.example.androidinterview.ui.payout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.androidinterview.R
import com.example.androidinterview.domain.biometric.BiometricAuthenticator
import com.example.androidinterview.domain.biometric.BiometricResult
import com.example.androidinterview.domain.model.Currency
import com.example.androidinterview.ui.payout.PayoutUiState.*
import com.example.androidinterview.util.formatMoney

@Composable
fun PayoutScreen(
    viewModel: PayoutViewModel = hiltViewModel(),
    biometricAuthenticator: BiometricAuthenticator
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                PayoutEffect.AuthenticateBiometric -> {
                    val currentActivity = activity
                    if (currentActivity == null) {
                        viewModel.onBiometricResult(
                            BiometricResult.Unavailable
                        )
                    } else {
                        biometricAuthenticator.authenticate(
                            activity = currentActivity,
                            onResult = viewModel::onBiometricResult,
                            onFailed = {
                                // Show "Fingerprint not recognized"
                            }
                        )
                    }
                }
            }
        }
    }

    when (val current = state) {
        is Form -> {
            PayoutForm(
                state = current,
                onAmountChanged = viewModel::onAmountChanged,
                onCurrencyChanged = viewModel::onCurrencyChanged,
                onIbanChanged = viewModel::onIbanChanged,
                onConfirm = viewModel::onConfirmClicked
            )
        }

        is Confirming -> {
            PayoutForm(
                state = Form(
                    data = current.data
                ),
                onAmountChanged = viewModel::onAmountChanged,
                onCurrencyChanged = viewModel::onCurrencyChanged,
                onIbanChanged = viewModel::onIbanChanged,
                onConfirm = viewModel::onConfirmClicked
            )

            ConfirmPayoutDialog(
                amount = current.data.amount,
                currency = current.data.currency,
                iban = current.data.iban,
                onCancel = viewModel::cancelConfirmation,
                onConfirm = viewModel::submitPayout
            )
        }
        is Submitting -> {
            LoadingContent()
        }

        is Success -> {
            PayoutSuccess(
                amount = current.payout.amount,
                currency = current.payout.currency,
                onCreateAnother = viewModel::createAnotherPayout
            )
        }

        is Error -> {
            PayoutFailure(
                message = payoutErrorMessage(current.error),
                onRetry = viewModel::retry
            )
        }

        is AwaitingBiometric -> {
            PayoutForm(
                state = PayoutUiState.Form(
                    data = current.data
                ),
                onAmountChanged = {},
                onCurrencyChanged = {},
                onIbanChanged = {},
                onConfirm = {}
            )
        }
    }
}

@Composable
private fun PayoutSuccess(
    amount: Int,
    currency: Currency,
    onCreateAnother: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            modifier = Modifier.height(160.dp)
        )
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = "Success",
            modifier = Modifier.size(64.dp),
            tint = colorResource(R.color.teal_200)
        )
        Spacer(
            modifier = Modifier.height(32.dp)
        )
        Text(
            text = stringResource(R.string.payout_completed),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = Bold,
        )
        Spacer(
            modifier = Modifier.height(16.dp)
        )
        Text(
            text = stringResource(
                R.string.your_payout_of_has_been_processed_successfully,
                formatMoney(
                    amount = amount
                        .toBigDecimal()
                        .movePointLeft(2)
                        .toPlainString(),
                    currency = currency
                )
            ),
            textAlign = TextAlign.Center
        )
        Spacer(
            modifier = Modifier.height(48.dp)
        )
        Button(
            onClick = onCreateAnother,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(stringResource(R.string.create_another_payout))
        }
    }
}

@Composable
private fun PayoutFailure(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            modifier = Modifier.height(160.dp)
        )
        Icon(
            imageVector = Icons.Filled.Clear,
            contentDescription = "Error",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(
            modifier = Modifier.height(32.dp)
        )
        Text(
            text = stringResource(R.string.unable_to_process_payout),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error,
            fontWeight = Bold,
        )
        Spacer(
            modifier = Modifier.height(16.dp)
        )
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(
            modifier = Modifier.height(48.dp)
        )
        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(stringResource(R.string.try_again))
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun payoutErrorMessage(error: PayoutError): String {
    return when (error) {
        PayoutError.InsufficientFunds ->
            stringResource(R.string.payout_insufficient_funds)

        PayoutError.ServiceUnavailable ->
            stringResource(R.string.payout_service_unavailable)

        PayoutError.ApiError ->
            stringResource(R.string.payout_unable_to_process)

        PayoutError.Unknown ->
            stringResource(R.string.payout_unknown_error)

        PayoutError.BiometricCancelled ->
            stringResource(R.string.biometric_authentication_cancelled)

        PayoutError.BiometricNotEnrolled ->
            stringResource(R.string.biometric_not_enrolled)

        PayoutError.BiometricUnavailable ->
            stringResource(R.string.biometric_unavailable)

        is PayoutError.BiometricFailed ->
            stringResource(R.string.biometric_authentication_failed)
    }
}
package com.example.androidinterview.ui.payout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.androidinterview.domain.model.Currency
import com.example.androidinterview.util.formatMoney

@Composable
fun PayoutScreen(
    viewModel: PayoutViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    when (val current = state) {
        is PayoutUiState.Form -> {
            PayoutForm(
                state = current,
                onAmountChanged = viewModel::onAmountChanged,
                onCurrencyChanged = viewModel::onCurrencyChanged,
                onIbanChanged = viewModel::onIbanChanged,
                onConfirm = viewModel::onConfirmClicked
            )
        }

        is PayoutUiState.Confirming -> {
            PayoutForm(
                state = PayoutUiState.Form(
                    amount = current.amount,
                    currency = current.currency,
                    iban = current.iban
                ),
                onAmountChanged = viewModel::onAmountChanged,
                onCurrencyChanged = viewModel::onCurrencyChanged,
                onIbanChanged = viewModel::onIbanChanged,
                onConfirm = viewModel::onConfirmClicked
            )

            ConfirmPayoutDialog(
                amount = current.amount,
                currency = current.currency,
                iban = current.iban,
                onCancel = viewModel::cancelConfirmation,
                onConfirm = viewModel::confirmPayout
            )
        }

        PayoutUiState.Submitting -> {
            PayoutSubmitting()
        }

        is PayoutUiState.Success -> {
            PayoutSuccess(
                amount = current.amount,
                currency = current.currency,
                onCreateAnother = viewModel::createAnotherPayout
            )
        }

        is PayoutUiState.Error -> {
            PayoutFailure(
                message = current.message,
                onRetry = viewModel::retry
            )
        }

        is PayoutUiState.InsufficientFunds -> {
            PayoutFailure(
                message = current.message,
                onRetry = viewModel::retry
            )
        }
    }
}

@Composable
private fun PayoutSuccess(
    amount: String,
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
        Text(
            text = "✓",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(
            modifier = Modifier.height(32.dp)
        )
        Text(
            text = "Payout Completed",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(
            modifier = Modifier.height(16.dp)
        )
        Text(
            text = "Your payout of ${formatMoney(amount.toInt(), currency)} has been processed successfully."
        )

        Spacer(
            modifier = Modifier.height(48.dp)
        )
        Button(
            onClick = onCreateAnother,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Another Payout")
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
        Text(
            text = "×",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(
            modifier = Modifier.height(32.dp)
        )
        Text(
            text = "Unable to Process Payout",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(
            modifier = Modifier.height(16.dp)
        )
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(
            modifier = Modifier.height(48.dp)
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Try Again")
        }
    }
}

@Composable
private fun PayoutSubmitting() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
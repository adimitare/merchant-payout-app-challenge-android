package com.example.androidinterview.ui.payout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.androidinterview.R
import com.example.androidinterview.domain.model.Currency
import com.example.androidinterview.util.formatMoney

@Composable
fun ConfirmPayoutDialog(
    amount: String,
    currency: Currency,
    iban: String,
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(
                stringResource(R.string.confirm_payout),
                modifier = Modifier.fillMaxWidth(),
                fontWeight = Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                ConfirmationRow(
                    label = stringResource(
                        R.string.amount_confirm_payout_dialog
                    ),
                    value = formatMoney(amount, currency)
                )

                ConfirmationRow(
                    label = stringResource(R.string.currency_confirm_payout_dialog),
                    value = currency.name
                )

                ConfirmationRow(
                    label = stringResource(R.string.iban_confirm_payout_dialog),
                    value = maskIban(iban)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm
            ) {
                Text(stringResource(R.string.confirm_text_button))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onCancel
            ) {
                Text(stringResource(R.string.cancel_text_button))
            }
        }
    )
}

@Composable
private fun ConfirmationRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(0.2f),
            maxLines = 1
        )
        Text(
            text = value,
            modifier = Modifier.weight(0.8f),
            maxLines = 1,
            textAlign = TextAlign.End,
            fontWeight = Bold
        )
    }
}

private fun maskIban(
    iban: String
): String {
    if (iban.length <= 8) {
        return iban
    }

    return iban.take(4) +
            "*".repeat(iban.length - 8) +
            iban.takeLast(4)
}
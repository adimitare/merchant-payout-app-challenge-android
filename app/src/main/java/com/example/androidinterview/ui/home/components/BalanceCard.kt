package com.example.androidinterview.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.androidinterview.R
import com.example.androidinterview.domain.model.Currency
import com.example.androidinterview.util.formatMoney

@Composable
fun BalanceCard(
    available: Int,
    pending: Int,
    currency: Currency
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BalanceItem(
            title = stringResource(R.string.available_text),
            amount = available,
            currency = currency
        )
        BalanceItem(
            title = stringResource(R.string.pending_text),
            amount = pending,
            currency = currency
        )
    }
}

@Composable
private fun BalanceItem(
    title: String,
    amount: Int,
    currency: Currency
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(
            modifier = Modifier.height(8.dp)
        )
        Text(
            text = formatMoney(amount, currency),
            style = MaterialTheme.typography.headlineLarge
        )
    }
}
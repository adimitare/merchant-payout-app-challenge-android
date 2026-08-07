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
import androidx.compose.ui.unit.dp

@Composable
fun BalanceCard(
    available: Int,
    pending: Int,
    currency: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BalanceItem(
            title = "Available",
            amount = available
        )


        BalanceItem(
            title = "Pending",
            amount = pending
        )
    }
}

@Composable
private fun BalanceItem(
    title: String,
    amount: Int
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
            text = "£%,.2f".format(amount / 100.0),
            style = MaterialTheme.typography.headlineLarge
        )
    }
}
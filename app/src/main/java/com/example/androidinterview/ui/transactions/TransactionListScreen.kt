package com.example.androidinterview.ui.transactions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.androidinterview.R
import com.example.androidinterview.domain.model.ActivityItem
import com.example.androidinterview.domain.usecase.TransactionDateLabel
import com.example.androidinterview.ui.common.AppStatusBar
import com.example.androidinterview.ui.common.ErrorContent
import com.example.androidinterview.ui.common.LoadingContent
import com.example.androidinterview.util.formatDate
import com.example.androidinterview.util.formatMoney
import java.time.format.DateTimeFormatter

@Composable
fun TransactionListScreen(
    onClose: () -> Unit,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val transactions = viewModel.transactions
        .collectAsLazyPagingItems()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TransactionHeader(
            onClose = onClose
        )

        when {
            transactions.loadState.refresh is LoadState.Loading -> {
                LoadingContent()
            }

            transactions.loadState.refresh is LoadState.Error -> {
                val error =
                    transactions.loadState.refresh as LoadState.Error

                ErrorContent(
                    message = error.error.message
                        ?: "Unable to load transactions",
                    retry = transactions::retry
                )
            }

            transactions.itemCount == 0 -> {
                EmptyTransactions()
            }

            else -> {
                TransactionList(
                    items = transactions
                )
            }
        }
    }
}

@Composable
private fun TransactionHeader(
    onClose: () -> Unit
) {
    AppStatusBar(
        title = stringResource(R.string.transactions_title),
        onBackClick = onClose
    )
}

@Composable
private fun TransactionList(
    items: LazyPagingItems<TransactionListItem>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            count = items.itemCount,
            key = { index ->
                when (val item = items[index]) {
                    is TransactionListItem.Header ->
                        "header_${item.date}"

                    is TransactionListItem.Transaction ->
                        item.activity.id

                    null -> index
                }
            }
        ) { index ->
            when (val item = items[index]) {
                is TransactionListItem.Header -> {
                    TransactionDateHeader(
                        date = item.date
                    )
                }

                is TransactionListItem.Transaction -> {
                    TransactionItem(
                        activity = item.activity
                    )
                }

                null -> Unit
            }
        }
        when (val appendState = items.loadState.append) {
            is LoadState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            is LoadState.Error -> {
                item {
                    TextButton(
                        onClick = items::retry,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Retry")
                    }
                }
            }

            is LoadState.NotLoading -> Unit
        }
    }
}

@Composable
private fun TransactionItem(
    activity: ActivityItem
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 24.dp,
                vertical = 16.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = activity.description,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = Bold,
            )
            Text(
                text = activity.type,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatDate(activity.date),
                style = MaterialTheme.typography.bodySmall
            )
        }
        // Right side
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = formatMoney(
                    amount = activity.amount,
                    currency = activity.currency
                ),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if(activity.amount >= 0)
                    Color(color = 0xFF16B955)
                else
                    MaterialTheme.colorScheme.error
            )
            Text(
                text = activity.status,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    HorizontalDivider()
}

@Composable
private fun EmptyTransactions() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No transactions yet",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun TransactionDateHeader(
    date: TransactionDateLabel
) {
    val title = when (date) {
        TransactionDateLabel.Today ->
            stringResource(R.string.transaction_date_today)
        TransactionDateLabel.Yesterday ->
            stringResource(R.string.transaction_date_yesterday)
        is TransactionDateLabel.Date ->
            date.value.format(
                DateTimeFormatter.ofPattern(stringResource(R.string.transaction_date_pattern))
            )
    }
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(
            horizontal = 24.dp,
            vertical = 12.dp
        )
    )
}
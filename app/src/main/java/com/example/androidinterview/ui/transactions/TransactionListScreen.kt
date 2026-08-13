package com.example.androidinterview.ui.transactions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.androidinterview.data.paging.AppendNotAllowedException
import com.example.androidinterview.R
import com.example.androidinterview.domain.model.ActivityItem
import com.example.androidinterview.domain.usecase.TransactionDateLabel
import com.example.androidinterview.ui.common.AppStatusBar
import com.example.androidinterview.ui.common.ErrorContent
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

        TransactionList(
            items = transactions,
            onScrolledNearEnd = viewModel::onScrolledNearEnd,
            modifier = Modifier.weight(1f)
        )
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
    items: LazyPagingItems<TransactionListItem>,
    onScrolledNearEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState, items.itemCount) {
        snapshotFlow {
            val firstVisibleIndex = listState.firstVisibleItemIndex
            val lastVisibleIndex =
                listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            Triple(firstVisibleIndex, lastVisibleIndex, items.itemCount)
        }.collect { (firstVisibleIndex, lastVisibleIndex, itemCount) ->
            val userScrolledNearEnd = firstVisibleIndex > 0 &&
                lastVisibleIndex != null &&
                itemCount > 0 &&
                lastVisibleIndex >= itemCount - 3

            if (!userScrolledNearEnd) {
                return@collect
            }

            onScrolledNearEnd()

            val appendState = items.loadState.append
            if (appendState is LoadState.Error &&
                appendState.error is AppendNotAllowedException
            ) {
                items.retry()
            } else if (appendState is LoadState.NotLoading &&
                !appendState.endOfPaginationReached
            ) {
                items.retry()
            }
        }
    }

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        LazyColumn(
            state = listState,
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
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp, bottom = 48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(36.dp),
                                strokeWidth = 5.dp
                            )
                            Text(
                                text = "Loading more...",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }

                is LoadState.Error -> {
                    if (appendState.error !is AppendNotAllowedException) {
                        item {
                            TextButton(
                                onClick = items::retry,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }

                is LoadState.NotLoading -> Unit
            }
        }

        when {
            items.loadState.refresh is LoadState.Loading && items.itemCount == 0 -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            items.loadState.refresh is LoadState.Error && items.itemCount == 0 -> {
                val error = items.loadState.refresh as LoadState.Error
                Box(modifier = Modifier.fillMaxSize()) {
                    ErrorContent(
                        message = error.error.message
                            ?: "Unable to load transactions",
                        retry = items::retry
                    )
                }
            }

            items.loadState.refresh is LoadState.NotLoading && items.itemCount == 0 -> {
                EmptyTransactions(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
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
private fun EmptyTransactions(
    modifier: Modifier = Modifier
) {
    Text(
        text = "No transactions yet",
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )
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
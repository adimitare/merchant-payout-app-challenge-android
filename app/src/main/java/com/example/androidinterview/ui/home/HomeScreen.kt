package com.example.androidinterview.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.androidinterview.R
import com.example.androidinterview.domain.model.Merchant
import com.example.androidinterview.ui.common.ErrorContent
import com.example.androidinterview.ui.common.LoadingContent
import com.example.androidinterview.ui.home.components.BalanceCard
import com.example.androidinterview.ui.home.components.RecentActivityItem

@Composable
fun HomeScreen(
    onOpenTransactions: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    when (val current = state) {
        HomeUiState.Loading -> {
            LoadingContent()
        }
        is HomeUiState.Error -> {
            ErrorContent(
                message = current.message,
                retry = viewModel::loadMerchant
            )
        }
        is HomeUiState.Success -> {
            HomeContent(
                merchant = current.merchant,
                onOpenTransactions = onOpenTransactions
            )
        }
    }
}

@Composable
private fun HomeContent(
    merchant: Merchant,
    onOpenTransactions: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(
                horizontal = 32.dp,
                vertical = 24.dp
            )
    ) {
        Text(
            text = stringResource(R.string.business_account),
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(
            modifier = Modifier.height(56.dp)
        )
        Text(
            text = stringResource(R.string.account_balance),
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(
            modifier = Modifier.height(32.dp)
        )
        BalanceCard(
            available = merchant.availableBalance,
            pending = merchant.pendingBalance,
            currency = merchant.currency
        )
        Spacer(
            modifier = Modifier.height(56.dp)
        )
        Text(
            text = stringResource(R.string.recent_activity),
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(
            modifier = Modifier.height(24.dp)
        )
        merchant.activityItem.forEach { activity ->
            RecentActivityItem(
                activityItem = activity
            )
            HorizontalDivider()
        }
        Spacer(
            modifier = Modifier.height(32.dp)
        )
        Button(
            onClick = onOpenTransactions,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFBBDEFB),
                contentColor = Color(0xFF1565C0),
            )
        ) {
            Text(
                text = stringResource(R.string.show_more),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
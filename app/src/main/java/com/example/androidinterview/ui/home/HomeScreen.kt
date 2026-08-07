package com.example.androidinterview.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.androidinterview.domain.model.Merchant
import com.example.androidinterview.ui.common.ErrorContent
import com.example.androidinterview.ui.common.LoadingContent
import com.example.androidinterview.ui.home.components.BalanceCard
import com.example.androidinterview.ui.home.components.RecentActivityItem


@Composable
fun HomeScreen(
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
                merchant = current.merchant
            )
        }
    }
}


@Composable
private fun HomeContent(
    merchant: Merchant
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
            text = "Business Account",
            style = MaterialTheme.typography.headlineLarge
        )


        Spacer(
            modifier = Modifier.height(56.dp)
        )


        Text(
            text = "Account Balance",
            style = MaterialTheme.typography.headlineSmall
        )


        Spacer(
            modifier = Modifier.height(32.dp)
        )

        BalanceCard(
            available = merchant.availableBalance,
            pending = merchant.pendingBalance,
            currency = merchant.currency.name
        )


        Spacer(
            modifier = Modifier.height(56.dp)
        )


        Text(
            text = "Recent Activity",
            style = MaterialTheme.typography.headlineSmall
        )


        Spacer(
            modifier = Modifier.height(24.dp)
        )


        merchant.activity.forEach { activity ->

            RecentActivityItem(
                activity = activity
            )

            HorizontalDivider()

        }
        Spacer(
            modifier = Modifier.height(32.dp)
        )
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Text(
                text = "Show More",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
package com.example.androidinterview.ui.payouts

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun PayoutsScreen(
    onShowTransactions: () -> Unit,
    viewModel: PayoutViewModel = hiltViewModel()
) {
    // ...
}
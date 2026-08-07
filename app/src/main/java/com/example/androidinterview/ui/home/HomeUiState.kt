package com.example.androidinterview.ui.home

import com.example.androidinterview.domain.model.Merchant

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val merchant: Merchant
    ) : HomeUiState
    data class Error(
        val message: String
    ) : HomeUiState
}
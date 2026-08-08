package com.example.androidinterview.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.androidinterview.domain.usecase.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    getTransactionsUseCase: GetTransactionsUseCase
) : ViewModel() {
    val transactions: Flow<PagingData<TransactionListItem>> =
        getTransactionsUseCase()
            .cachedIn(viewModelScope)
}
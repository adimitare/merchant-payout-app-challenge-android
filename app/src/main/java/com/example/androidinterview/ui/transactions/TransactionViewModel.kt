package com.example.androidinterview.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.androidinterview.data.paging.TransactionAppendGate
import com.example.androidinterview.domain.usecase.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    getTransactionsUseCase: GetTransactionsUseCase,
    private val appendGate: TransactionAppendGate
) : ViewModel() {
    val transactions: Flow<PagingData<TransactionListItem>> =
        getTransactionsUseCase()
            .cachedIn(viewModelScope)

    fun onScrolledNearEnd() {
        appendGate.markUserScrolled()
    }
}
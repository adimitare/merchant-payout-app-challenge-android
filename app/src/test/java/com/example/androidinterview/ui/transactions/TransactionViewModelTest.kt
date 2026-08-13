package com.example.androidinterview.ui.transactions

import androidx.paging.PagingData
import com.example.androidinterview.data.paging.TransactionAppendGate
import com.example.androidinterview.domain.usecase.GetTransactionsUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionViewModelTest {

    private lateinit var getTransactionsUseCase: GetTransactionsUseCase

    private lateinit var viewModel: TransactionViewModel

    @Before
    fun setup() {
        getTransactionsUseCase = mockk()
        every {
            getTransactionsUseCase()
        } returns flowOf(
            PagingData.empty<TransactionListItem>()
        )
        viewModel = TransactionViewModel(
            getTransactionsUseCase = getTransactionsUseCase,
            appendGate = TransactionAppendGate()
        )
    }

    @Test
    fun init_callsGetTransactionsUseCase() {
        verify(exactly = 1) {
            getTransactionsUseCase()
        }
    }

    @Test
    fun transactions_isNotNull() {
        assertNotNull(viewModel.transactions)
    }

    @Test
    fun transactions_emitsPagingData() = runTest {
        val result = viewModel.transactions
        assertNotNull(result)
    }
}
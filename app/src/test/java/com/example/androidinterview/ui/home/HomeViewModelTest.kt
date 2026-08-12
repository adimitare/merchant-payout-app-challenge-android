package com.example.androidinterview.ui.home

import com.example.androidinterview.MainDispatcherRule
import com.example.androidinterview.domain.model.Currency
import com.example.androidinterview.domain.model.Merchant
import com.example.androidinterview.domain.usecase.GetMerchantUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getMerchantUseCase: GetMerchantUseCase

    @Before
    fun setup() {
        getMerchantUseCase = mockk()
    }

    @Test
    fun `initial state is Loading`() {
        coEvery {
            getMerchantUseCase()
        } returns merchant()

        val viewModel = HomeViewModel(
            getMerchantUseCase = getMerchantUseCase
        )

        assertEquals(
            HomeUiState.Loading,
            viewModel.uiState.value
        )
    }

    @Test
    fun `loadMerchant emits Success when use case succeeds`() = runTest {
        val merchant = merchant()

        coEvery {
            getMerchantUseCase()
        } returns merchant

        val viewModel = HomeViewModel(
            getMerchantUseCase = getMerchantUseCase
        )

        advanceUntilIdle()

        assertEquals(
            HomeUiState.Success(merchant),
            viewModel.uiState.value
        )

        coVerify(exactly = 1) {
            getMerchantUseCase()
        }
    }

    @Test
    fun `loadMerchant emits Error when use case fails`() = runTest {
        val exception = RuntimeException("Network error")

        coEvery {
            getMerchantUseCase()
        } throws exception

        val viewModel = HomeViewModel(
            getMerchantUseCase = getMerchantUseCase
        )

        advanceUntilIdle()

        assertEquals(
            HomeUiState.Error("Network error"),
            viewModel.uiState.value
        )

        coVerify(exactly = 1) {
            getMerchantUseCase()
        }
    }

    @Test
    fun `loadMerchant uses Unknown error when exception has no message`() = runTest {
        coEvery {
            getMerchantUseCase()
        } throws RuntimeException()

        val viewModel = HomeViewModel(
            getMerchantUseCase = getMerchantUseCase
        )

        advanceUntilIdle()

        assertEquals(
            HomeUiState.Error("Unknown error"),
            viewModel.uiState.value
        )
    }

    @Test
    fun `loadMerchant can be called again`() = runTest {
        val firstMerchant = merchant(
            availableBalance = 1000
        )

        val secondMerchant = merchant(
            availableBalance = 2000
        )

        coEvery {
            getMerchantUseCase()
        } returnsMany listOf(
            firstMerchant,
            secondMerchant
        )

        val viewModel = HomeViewModel(
            getMerchantUseCase = getMerchantUseCase
        )

        advanceUntilIdle()

        assertEquals(
            HomeUiState.Success(firstMerchant),
            viewModel.uiState.value
        )

        viewModel.loadMerchant()

        advanceUntilIdle()

        assertEquals(
            HomeUiState.Success(secondMerchant),
            viewModel.uiState.value
        )

        coVerify(exactly = 2) {
            getMerchantUseCase()
        }
    }

    private fun merchant(
        availableBalance: Int = 1000,
        pendingBalance: Int = 250
    ) = Merchant(
        availableBalance = availableBalance,
        pendingBalance = pendingBalance,
        currency = Currency.GBP,
        activityItem = emptyList()
    )
}
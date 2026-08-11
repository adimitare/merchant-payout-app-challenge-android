package com.example.androidinterview.domain.usecase

import com.example.androidinterview.domain.model.PayoutResult
import com.example.androidinterview.domain.repository.PayoutRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SubmitPayoutUseCaseTest {
    private lateinit var repository: PayoutRepository
    private lateinit var useCase: SubmitPayoutUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = SubmitPayoutUseCase(repository)
    }

    @Test
    fun `invoke returns payout result from repository`() = runTest {
        // Given
        val amount = 100
        val currency = "EUR"
        val iban = "BG80BNBG96611020345678"
        val expectedResult = mockk<PayoutResult>()
        coEvery {
            repository.createPayout(
                amount = amount,
                currency = currency,
                iban = iban
            )
        } returns expectedResult
        // When
        val result = useCase(
            amount = amount,
            currency = currency,
            iban = iban
        )
        // Then
        assertEquals(expectedResult, result)
    }

    @Test
    fun `invoke calls repository createPayout with correct arguments`() = runTest {
        // Given
        val amount = 100
        val currency = "EUR"
        val iban = "BG80BNBG96611020345678"
        val payoutResult = mockk<PayoutResult>()
        coEvery {
            repository.createPayout(
                amount = amount,
                currency = currency,
                iban = iban
            )
        } returns payoutResult
        // When
        useCase(
            amount = amount,
            currency = currency,
            iban = iban
        )
        // Then
        coVerify(exactly = 1) {
            repository.createPayout(
                amount = amount,
                currency = currency,
                iban = iban
            )
        }
    }
}
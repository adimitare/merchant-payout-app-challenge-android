package com.example.androidinterview.domain.usecase

import com.example.androidinterview.domain.model.Merchant
import com.example.androidinterview.domain.repository.MerchantRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetMerchantUseCaseTest {
    private lateinit var repository: MerchantRepository
    private lateinit var useCase: GetMerchantUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetMerchantUseCase(repository)
    }

    @Test
    fun `invoke returns merchant from repository`() = runTest {
        // Given
        val expectedMerchant = mockk<Merchant>()
        coEvery { repository.getMerchant() } returns expectedMerchant
        // When
        val result = useCase()
        // Then
        assertEquals(expectedMerchant, result)
    }

    @Test
    fun `invoke calls repository getMerchant exactly once`() = runTest {
        // Given
        val merchant = mockk<Merchant>()
        coEvery { repository.getMerchant() } returns merchant
        // When
        useCase()
        // Then
        coVerify(exactly = 1) {
            repository.getMerchant()
        }
    }
}
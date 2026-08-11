package com.example.androidinterview.data.repository

import com.example.androidinterview.data.remote.MerchantApi
import com.example.androidinterview.data.remote.dto.MerchantResponseDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MerchantRepositoryImplTest {
    private lateinit var api: MerchantApi
    private lateinit var repository: MerchantRepositoryImpl

    @Before
    fun setup() {
        api = mockk()
        repository = MerchantRepositoryImpl(api)
    }

    @Test
    fun `getMerchant returns mapped merchant`() = runTest {
        // Given
        val response = MerchantResponseDto(
            availableBalance = 1000,
            pendingBalance = 250,
            currency = "GBP",
            activity = emptyList()
        )
        coEvery {
            api.getMerchant()
        } returns response
        // When
        val result = repository.getMerchant()
        // Then
        assertEquals(
            1000,
            result.availableBalance
        )
        assertEquals(
            250,
            result.pendingBalance
        )
        assertEquals(
            "GBP",
            result.currency.toString()
        )
        coVerify(exactly = 1) {
            api.getMerchant()
        }
    }

    @Test
    fun `getMerchant returns zero balances`() = runTest {
        // Given
        val response = MerchantResponseDto(
            availableBalance = 0,
            pendingBalance = 0,
            currency = "GBP",
            activity = emptyList()
        )
        coEvery {
            api.getMerchant()
        } returns response
        // When
        val result = repository.getMerchant()
        // Then
        assertEquals(
            0,
            result.availableBalance
        )
        assertEquals(
            0,
            result.pendingBalance
        )
        assertEquals(
            "GBP",
            result.currency.toString()
        )
    }

    @Test
    fun `getMerchant calls api exactly once`() = runTest {
        // Given
        val response = MerchantResponseDto(
            availableBalance = 1000,
            pendingBalance = 250,
            currency = "GBP",
            activity = emptyList()
        )
        coEvery {
            api.getMerchant()
        } returns response
        // When
        repository.getMerchant()
        // Then
        coVerify(exactly = 1) {
            api.getMerchant()
        }
    }

    @Test
    fun `getMerchant propagates api exception`() = runTest {
        // Given
        val exception = RuntimeException("Network error")
        coEvery {
            api.getMerchant()
        } throws exception
        // When
        val thrown = try {
            repository.getMerchant()
            null
        } catch (e: RuntimeException) {
            e
        }
        // Then
        assertEquals(
            "Network error",
            thrown?.message
        )
        coVerify(exactly = 1) {
            api.getMerchant()
        }
    }
}
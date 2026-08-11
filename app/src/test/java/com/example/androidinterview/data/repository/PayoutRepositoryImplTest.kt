package com.example.androidinterview.data.repository

import com.example.androidinterview.data.remote.MerchantApi
import com.example.androidinterview.data.remote.dto.CreatePayoutRequestDto
import com.example.androidinterview.data.remote.dto.CreatePayoutResponseDto
import com.example.androidinterview.domain.model.PayoutException
import com.example.androidinterview.domain.model.PayoutStatus
import com.example.androidinterview.domain.repository.DeviceRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class PayoutRepositoryImplTest {
    private lateinit var api: MerchantApi
    private lateinit var deviceRepository: DeviceRepository
    private lateinit var repository: PayoutRepositoryImpl

    @Before
    fun setup() {
        api = mockk()
        deviceRepository = mockk()
        repository = PayoutRepositoryImpl(
            api = api,
            deviceRepository = deviceRepository
        )
    }

    @Test
    fun `createPayout returns mapped payout result on success`() = runTest {
        // Given
        val amount = 100
        val currency = "GBP"
        val iban = "GB123456789"
        val deviceId = "device-123"
        val createdAt = "2024-01-01T12:00:00Z"
        val response = CreatePayoutResponseDto(
            id = "payout-123",
            status = "COMPLETED",
            amount = amount,
            currency = currency,
            iban = iban,
            createdAt = createdAt
        )
        coEvery {
            deviceRepository.getDeviceId()
        } returns deviceId
        coEvery {
            api.createPayout(
                CreatePayoutRequestDto(
                    amount = amount,
                    currency = currency,
                    iban = iban,
                    deviceId = deviceId
                )
            )
        } returns response
        // When
        val result = repository.createPayout(
            amount = amount,
            currency = currency,
            iban = iban
        )
        // Then
        assertEquals(
            "payout-123",
            result.id
        )
        assertEquals(
            amount,
            result.amount
        )
        assertEquals(
            "GBP",
            result.currency.toString()
        )
        assertEquals(
            iban,
            result.iban
        )
        assertEquals(
            PayoutStatus.COMPLETED,
            result.status
        )
        assertEquals(
            createdAt,
            result.createdAt
        )
        coVerify(exactly = 1) {
            deviceRepository.getDeviceId()
        }
        coVerify(exactly = 1) {
            api.createPayout(
                CreatePayoutRequestDto(
                    amount = amount,
                    currency = currency,
                    iban = iban,
                    deviceId = deviceId
                )
            )
        }
        confirmVerified(
            api,
            deviceRepository
        )
    }

    @Test
    fun `createPayout sends device id from device repository`() = runTest {
        // Given
        val deviceId = "my-device-id"
        val response = CreatePayoutResponseDto(
            id = "payout-123",
            status = "PENDING",
            amount = 500,
            currency = "GBP",
            iban = "GB123456789",
            createdAt = "2024-01-01T12:00:00Z"
        )
        coEvery {
            deviceRepository.getDeviceId()
        } returns deviceId
        coEvery {
            api.createPayout(any())
        } returns response
        // When
        repository.createPayout(
            amount = 500,
            currency = "GBP",
            iban = "GB123456789"
        )
        // Then
        coVerify(exactly = 1) {
            api.createPayout(
                CreatePayoutRequestDto(
                    amount = 500,
                    currency = "GBP",
                    iban = "GB123456789",
                    deviceId = deviceId
                )
            )
        }
    }

    @Test
    fun `createPayout throws InsufficientFunds for 400`() = runTest {
        // Given
        coEvery {
            deviceRepository.getDeviceId()
        } returns "device-123"
        coEvery {
            api.createPayout(any())
        } throws httpException(
            code = 400,
            errorBody = """{"error":"Insufficient funds"}"""
        )
        // When
        val exception = try {
            repository.createPayout(
                amount = 100,
                currency = "GBP",
                iban = "GB123456789"
            )
            null
        } catch (exception: PayoutException) {
            exception
        }
        // Then
        assertEquals(
            PayoutException.InsufficientFunds,
            exception
        )
    }

    @Test
    fun `createPayout throws ServiceUnavailable for 503`() = runTest {
        // Given
        coEvery {
            deviceRepository.getDeviceId()
        } returns "device-123"
        coEvery {
            api.createPayout(any())
        } throws httpException(
            code = 503,
            errorBody = """{"error":"Service unavailable"}"""
        )
        // When
        val exception = try {
            repository.createPayout(
                amount = 100,
                currency = "GBP",
                iban = "GB123456789"
            )
            null
        } catch (exception: PayoutException) {
            exception
        }
        // Then
        assertEquals(
            PayoutException.ServiceUnavailable,
            exception
        )
    }

    @Test
    fun `createPayout throws ApiError with parsed error message`() = runTest {
        // Given
        coEvery {
            deviceRepository.getDeviceId()
        } returns "device-123"

        coEvery {
            api.createPayout(any())
        } throws httpException(
            code = 500,
            errorBody = """{"error":"Something went wrong"}"""
        )
        // When
        val exception = try {
            repository.createPayout(
                amount = 100,
                currency = "GBP",
                iban = "GB123456789"
            )
            null
        } catch (exception: PayoutException) {
            exception
        }
        // Then
        assertTrue(
            exception is PayoutException.ApiError
        )
        assertEquals(
            "Something went wrong",
            (exception as PayoutException.ApiError).errorMessage
        )
    }

    @Test
    fun `createPayout uses http message when error body is invalid`() =
        runTest {
            // Given
            coEvery {
                deviceRepository.getDeviceId()
            } returns "device-123"
            val httpException = httpException(
                code = 500,
                errorBody = "invalid json"
            )
            coEvery {
                api.createPayout(any())
            } throws httpException
            // When
            val exception = try {
                repository.createPayout(
                    amount = 100,
                    currency = "GBP",
                    iban = "GB123456789"
                )
                null
            } catch (exception: PayoutException) {
                exception
            }
            // Then
            assertTrue(
                exception is PayoutException.ApiError
            )
            assertEquals(
                httpException.message(),
                (exception as PayoutException.ApiError).errorMessage
            )
        }

    @Test
    fun `createPayout uses http message when error body is empty`() =
        runTest {
            // Given
            coEvery {
                deviceRepository.getDeviceId()
            } returns "device-123"

            val httpException = httpException(
                code = 500,
                errorBody = ""
            )
            coEvery {
                api.createPayout(any())
            } throws httpException
            // When
            val exception = try {
                repository.createPayout(
                    amount = 100,
                    currency = "GBP",
                    iban = "GB123456789"
                )
                null
            } catch (exception: PayoutException) {
                exception
            }
            // Then
            assertTrue(
                exception is PayoutException.ApiError
            )
            assertEquals(
                httpException.message(),
                (exception as PayoutException.ApiError).errorMessage
            )
        }

    private fun httpException(
        code: Int,
        errorBody: String
    ): HttpException {
        val responseBody = errorBody.toResponseBody(
            "application/json".toMediaType()
        )
        return HttpException(
            Response.error<Any>(
                code,
                responseBody
            )
        )
    }
}
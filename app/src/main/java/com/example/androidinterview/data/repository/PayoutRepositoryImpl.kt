package com.example.androidinterview.data.repository

import com.example.androidinterview.data.mapper.toDomain
import com.example.androidinterview.data.remote.MerchantApi
import com.example.androidinterview.data.remote.dto.CreatePayoutRequestDto
import com.example.androidinterview.data.remote.dto.ErrorResponseDto
import com.example.androidinterview.domain.model.PayoutException
import com.example.androidinterview.domain.model.PayoutResult
import com.example.androidinterview.domain.repository.DeviceRepository
import com.example.androidinterview.domain.repository.PayoutRepository
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import javax.inject.Inject

class PayoutRepositoryImpl @Inject constructor(
    private val api: MerchantApi,
    private val deviceRepository: DeviceRepository
) : PayoutRepository {

    override suspend fun createPayout(
        amount: Int,
        currency: String,
        iban: String
    ): PayoutResult {
        val deviceId = deviceRepository.getDeviceId()
        return try {
            api.createPayout(
                CreatePayoutRequestDto(
                    amount = amount,
                    currency = currency,
                    iban = iban,
                    deviceId = deviceId
                )
            ).toDomain()
        } catch (exception: HttpException) {
            throw exception.toPayoutException()
        }
    }

    private fun HttpException.toPayoutException(): PayoutException {
        val errorMessage = parseErrorMessage()
        return when (code()) {
            400 -> PayoutException.InsufficientFunds
            503 -> PayoutException.ServiceUnavailable
            else -> PayoutException.ApiError(
                errorMessage = errorMessage
            )
        }
    }

    private fun HttpException.parseErrorMessage(): String {
        val rawBody = response()?.errorBody()?.string()

        return runCatching {
            rawBody
                ?.takeIf { it.isNotBlank() }
                ?.let { json.decodeFromString<ErrorResponseDto>(it).error }
                ?.takeIf { it.isNotBlank() }
        }.getOrNull() ?: message()
    }

    private companion object {
        val json = Json {
            ignoreUnknownKeys = true
        }
    }
}
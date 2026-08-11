package com.example.androidinterview.data.mapper

import com.example.androidinterview.data.remote.dto.ActivityItemDto
import com.example.androidinterview.data.remote.dto.CreatePayoutResponseDto
import com.example.androidinterview.data.remote.dto.MerchantResponseDto
import com.example.androidinterview.domain.model.ActivityStatus
import com.example.androidinterview.domain.model.ActivityType
import com.example.androidinterview.domain.model.Currency
import com.example.androidinterview.domain.model.PayoutStatus
import com.example.androidinterview.util.toFriendlyStatus
import com.example.androidinterview.util.toFriendlyType
import org.junit.Assert.assertEquals
import org.junit.Test

class DataMapperTest {

    @Test
    fun `MerchantResponseDto toDomain maps merchant fields correctly`() {
        // Given
        val activity = ActivityItemDto(
            id = "activity-1",
            type = "PAYOUT",
            amount = 100,
            currency = "GBP",
            date = "2024-01-15",
            description = "Card payment",
            status = "COMPLETED"
        )
        val dto = MerchantResponseDto(
            availableBalance = 1000,
            pendingBalance = 250,
            currency = "GBP",
            activity = listOf(activity)
        )
        // When
        val result = dto.toDomain()
        // Then
        assertEquals(1000, result.availableBalance)
        assertEquals(250, result.pendingBalance)
        assertEquals(Currency.GBP, result.currency)
        assertEquals(1, result.activityItem.size)
        val mappedActivity = result.activityItem.first()
        assertEquals("activity-1", mappedActivity.id)
        assertEquals(
            ActivityType.PAYOUT.toFriendlyType(),
            mappedActivity.type
        )
        assertEquals(100, mappedActivity.amount)
        assertEquals(Currency.GBP, mappedActivity.currency)
        assertEquals("2024-01-15", mappedActivity.date)
        assertEquals("Card payment", mappedActivity.description)
        assertEquals(
            ActivityStatus.COMPLETED.toFriendlyStatus(),
            mappedActivity.status
        )
    }

    @Test
    fun `MerchantResponseDto toDomain maps empty activity list`() {
        // Given
        val dto = MerchantResponseDto(
            availableBalance = 1000,
            pendingBalance = 250,
            currency = "GBP",
            activity = emptyList()
        )
        // When
        val result = dto.toDomain()
        // Then
        assertEquals(1000, result.availableBalance)
        assertEquals(250, result.pendingBalance)
        assertEquals(Currency.GBP, result.currency)
        assertEquals(0, result.activityItem.size)
    }

    @Test
    fun `CreatePayoutResponseDto toDomain maps completed status`() {
        // Given
        val dto = CreatePayoutResponseDto(
            id = "payout-1",
            amount = 500,
            currency = "GBP",
            iban = "GB29NWBK60161331926819",
            status = "COMPLETED",
            createdAt = "2024-01-15T10:00:00Z"
        )
        // When
        val result = dto.toDomain()
        // Then
        assertEquals("payout-1", result.id)
        assertEquals(500, result.amount)
        assertEquals(Currency.GBP, result.currency)
        assertEquals(
            "GB29NWBK60161331926819",
            result.iban
        )
        assertEquals(PayoutStatus.COMPLETED, result.status)
        assertEquals(
            "2024-01-15T10:00:00Z",
            result.createdAt
        )
    }

    @Test
    fun `CreatePayoutResponseDto toDomain maps failed status`() {
        // Given
        val dto = CreatePayoutResponseDto(
            id = "payout-1",
            amount = 500,
            currency = "GBP",
            iban = "GB29NWBK60161331926819",
            status = "FAILED",
            createdAt = "2024-01-15T10:00:00Z"
        )
        // When
        val result = dto.toDomain()
        // Then
        assertEquals(PayoutStatus.FAILED, result.status)
    }

    @Test
    fun `CreatePayoutResponseDto toDomain maps unknown status to pending`() {
        // Given
        val dto = CreatePayoutResponseDto(
            id = "payout-1",
            amount = 500,
            currency = "GBP",
            iban = "GB29NWBK60161331926819",
            status = "UNKNOWN",
            createdAt = "2024-01-15T10:00:00Z"
        )
        // When
        val result = dto.toDomain()
        // Then
        assertEquals(PayoutStatus.PENDING, result.status)
    }

    @Test
    fun `CreatePayoutResponseDto toDomain maps lowercase completed status`() {
        // Given
        val dto = CreatePayoutResponseDto(
            id = "payout-1",
            amount = 500,
            currency = "GBP",
            iban = "GB29NWBK60161331926819",
            status = "completed",
            createdAt = "2024-01-15T10:00:00Z"
        )
        // When
        val result = dto.toDomain()
        // Then
        assertEquals(PayoutStatus.COMPLETED, result.status)
    }

    @Test
    fun `CreatePayoutResponseDto toDomain maps lowercase failed status`() {
        // Given
        val dto = CreatePayoutResponseDto(
            id = "payout-1",
            amount = 500,
            currency = "GBP",
            iban = "GB29NWBK60161331926819",
            status = "failed",
            createdAt = "2024-01-15T10:00:00Z"
        )
        // When
        val result = dto.toDomain()
        // Then
        assertEquals(PayoutStatus.FAILED, result.status)
    }

    @Test
    fun `CreatePayoutResponseDto toDomain maps processing status to pending`() {
        // Given
        val dto = CreatePayoutResponseDto(
            id = "payout-1",
            amount = 500,
            currency = "GBP",
            iban = "GB29NWBK60161331926819",
            status = "PROCESSING",
            createdAt = "2024-01-15T10:00:00Z"
        )
        // When
        val result = dto.toDomain()
        // Then
        assertEquals(PayoutStatus.PENDING, result.status)
    }
}
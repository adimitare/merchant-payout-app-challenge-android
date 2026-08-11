package com.example.androidinterview.data.local.mapper

import com.example.androidinterview.data.local.entity.ActivityEntity
import com.example.androidinterview.data.remote.dto.ActivityItemDto
import com.example.androidinterview.domain.model.ActivityStatus
import com.example.androidinterview.domain.model.ActivityType
import com.example.androidinterview.domain.model.Currency
import com.example.androidinterview.util.toFriendlyStatus
import com.example.androidinterview.util.toFriendlyType
import org.junit.Assert.assertEquals
import org.junit.Test

class LocalMapperTest {

    @Test
    fun `ActivityEntity toDomain maps all fields correctly`() {
        // Given
        val entity = ActivityEntity(
            id = "activity-1",
            type = "PAYOUT",
            amount = 100,
            currency = "GBP",
            date = "2024-01-15",
            description = "Bank transfer",
            status = "COMPLETED"
        )

        // When
        val result = entity.toDomain()

        // Then
        assertEquals("activity-1", result.id)
        assertEquals(
            ActivityType.PAYOUT.toFriendlyType(),
            result.type
        )
        assertEquals(100, result.amount)
        assertEquals(Currency.GBP, result.currency)
        assertEquals("2024-01-15", result.date)
        assertEquals("Bank transfer", result.description)
        assertEquals(
            ActivityStatus.COMPLETED.toFriendlyStatus(),
            result.status
        )
    }

    @Test
    fun `ActivityEntity toDomain handles lowercase enum values`() {
        // Given
        val entity = ActivityEntity(
            id = "activity-1",
            type = "payout",
            amount = 100,
            currency = "gbp",
            date = "2024-01-15",
            description = "Bank transfer",
            status = "completed"
        )

        // When
        val result = entity.toDomain()

        // Then
        assertEquals(
            ActivityType.PAYOUT.toFriendlyType(),
            result.type
        )
        assertEquals(Currency.GBP, result.currency)
        assertEquals(
            ActivityStatus.COMPLETED.toFriendlyStatus(),
            result.status
        )
    }

    @Test
    fun `ActivityItemDto toEntity maps all fields correctly`() {
        // Given
        val dto = ActivityItemDto(
            id = "activity-1",
            type = "PAYOUT",
            amount = 100,
            currency = "GBP",
            date = "2024-01-15",
            description = "Bank transfer",
            status = "COMPLETED"
        )

        // When
        val result = dto.toEntity()

        // Then
        assertEquals(
            ActivityEntity(
                id = "activity-1",
                type = "PAYOUT",
                amount = 100,
                currency = "GBP",
                date = "2024-01-15",
                description = "Bank transfer",
                status = "COMPLETED"
            ),
            result
        )
    }
}
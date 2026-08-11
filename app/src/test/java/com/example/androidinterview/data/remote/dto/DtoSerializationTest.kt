package com.example.androidinterview.data.remote.dto

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class DtoSerializationTest {

    private lateinit var json: Json

    @Before
    fun setup() {
        json = Json {
            ignoreUnknownKeys = true
        }
    }

    @Test
    fun `ActivityItemDto serializes and deserializes correctly`() {
        // Given
        val dto = ActivityItemDto(
            id = "activity-1",
            type = "PAYOUT",
            amount = 500,
            currency = "GBP",
            date = "2024-01-15",
            description = "Bank transfer",
            status = "COMPLETED"
        )

        // When
        val encoded = json.encodeToString(dto)
        val decoded = json.decodeFromString<ActivityItemDto>(encoded)

        // Then
        assertEquals(dto, decoded)
    }

    @Test
    fun `ActivityResponseDto deserializes snake case fields correctly`() {
        // Given
        val jsonResponse = """
            {
                "items": [
                    {
                        "id": "activity-1",
                        "type": "PAYOUT",
                        "amount": 500,
                        "currency": "GBP",
                        "date": "2024-01-15",
                        "description": "Bank transfer",
                        "status": "COMPLETED"
                    }
                ],
                "next_cursor": "cursor-2",
                "has_more": true
            }
        """.trimIndent()

        // When
        val result =
            json.decodeFromString<ActivityResponseDto>(jsonResponse)

        // Then
        assertEquals(1, result.items.size)
        assertEquals("activity-1", result.items.first().id)
        assertEquals("PAYOUT", result.items.first().type)
        assertEquals(500, result.items.first().amount)
        assertEquals("GBP", result.items.first().currency)
        assertEquals("cursor-2", result.nextCursor)
        assertEquals(true, result.hasMore)
    }

    @Test
    fun `ActivityResponseDto handles null next cursor`() {
        // Given
        val jsonResponse = """
            {
                "items": [],
                "next_cursor": null,
                "has_more": false
            }
        """.trimIndent()

        // When
        val result =
            json.decodeFromString<ActivityResponseDto>(jsonResponse)

        // Then
        assertEquals(emptyList<ActivityItemDto>(), result.items)
        assertNull(result.nextCursor)
        assertEquals(false, result.hasMore)
    }

    @Test
    fun `ActivityResponseDto serializes snake case fields correctly`() {
        // Given
        val dto = ActivityResponseDto(
            items = emptyList(),
            nextCursor = "cursor-2",
            hasMore = true
        )

        // When
        val result = json.encodeToString(dto)

        // Then
        assertEquals(true, result.contains("\"next_cursor\":\"cursor-2\""))
        assertEquals(true, result.contains("\"has_more\":true"))
    }

    @Test
    fun `CreatePayoutRequestDto serializes correctly`() {
        // Given
        val dto = CreatePayoutRequestDto(
            amount = 500,
            currency = "GBP",
            iban = "GB29NWBK60161331926819",
            deviceId = "device-123"
        )

        // When
        val encoded = json.encodeToString(dto)
        val decoded =
            json.decodeFromString<CreatePayoutRequestDto>(encoded)

        // Then
        assertEquals(dto, decoded)
        assertEquals(true, encoded.contains("\"deviceId\":\"device-123\""))
    }

    @Test
    fun `CreatePayoutResponseDto deserializes created_at correctly`() {
        // Given
        val jsonResponse = """
            {
                "id": "payout-1",
                "status": "COMPLETED",
                "amount": 500,
                "currency": "GBP",
                "iban": "GB29NWBK60161331926819",
                "created_at": "2024-01-15T10:00:00Z"
            }
        """.trimIndent()

        // When
        val result =
            json.decodeFromString<CreatePayoutResponseDto>(jsonResponse)

        // Then
        assertEquals("payout-1", result.id)
        assertEquals("COMPLETED", result.status)
        assertEquals(500, result.amount)
        assertEquals("GBP", result.currency)
        assertEquals(
            "GB29NWBK60161331926819",
            result.iban
        )
        assertEquals(
            "2024-01-15T10:00:00Z",
            result.createdAt
        )
    }

    @Test
    fun `CreatePayoutResponseDto serializes created_at correctly`() {
        // Given
        val dto = CreatePayoutResponseDto(
            id = "payout-1",
            status = "COMPLETED",
            amount = 500,
            currency = "GBP",
            iban = "GB29NWBK60161331926819",
            createdAt = "2024-01-15T10:00:00Z"
        )

        // When
        val encoded = json.encodeToString(dto)

        // Then
        assertEquals(
            true,
            encoded.contains(
                "\"created_at\":\"2024-01-15T10:00:00Z\""
            )
        )
    }

    @Test
    fun `DeviceDto deserializes device_id correctly`() {
        // Given
        val jsonResponse = """
            {
                "device_id": "device-123"
            }
        """.trimIndent()

        // When
        val result =
            json.decodeFromString<DeviceDto>(jsonResponse)

        // Then
        assertEquals("device-123", result.deviceId)
    }

    @Test
    fun `DeviceDto serializes device_id correctly`() {
        // Given
        val dto = DeviceDto(
            deviceId = "device-123"
        )

        // When
        val encoded = json.encodeToString(dto)

        // Then
        assertEquals(
            true,
            encoded.contains("\"device_id\":\"device-123\"")
        )
    }

    @Test
    fun `ErrorResponseDto deserializes complete error response`() {
        // Given
        val jsonResponse = """
            {
                "code": "INVALID_IBAN",
                "error": "The provided IBAN is invalid"
            }
        """.trimIndent()

        // When
        val result =
            json.decodeFromString<ErrorResponseDto>(jsonResponse)

        // Then
        assertEquals("INVALID_IBAN", result.code)
        assertEquals(
            "The provided IBAN is invalid",
            result.error
        )
    }

    @Test
    fun `ErrorResponseDto allows missing fields`() {
        // Given
        val jsonResponse = "{}"

        // When
        val result =
            json.decodeFromString<ErrorResponseDto>(jsonResponse)

        // Then
        assertNull(result.code)
        assertNull(result.error)
    }

    @Test
    fun `ErrorResponseDto supports code without error`() {
        // Given
        val jsonResponse = """
            {
                "code": "UNKNOWN_ERROR"
            }
        """.trimIndent()

        // When
        val result =
            json.decodeFromString<ErrorResponseDto>(jsonResponse)

        // Then
        assertEquals("UNKNOWN_ERROR", result.code)
        assertNull(result.error)
    }

    @Test
    fun `MerchantResponseDto serializes and deserializes correctly`() {
        // Given
        val dto = MerchantResponseDto(
            availableBalance = 1000,
            pendingBalance = 250,
            currency = "GBP",
            activity = listOf(
                ActivityItemDto(
                    id = "activity-1",
                    type = "PAYOUT",
                    amount = 500,
                    currency = "GBP",
                    date = "2024-01-15",
                    description = "Bank transfer",
                    status = "COMPLETED"
                ),
                ActivityItemDto(
                    id = "activity-2",
                    type = "FEE",
                    amount = 25,
                    currency = "GBP",
                    date = "2024-01-16",
                    description = "Transaction fee",
                    status = "COMPLETED"
                )
            )
        )

        // When
        val encoded = json.encodeToString(dto)
        val decoded =
            json.decodeFromString<MerchantResponseDto>(encoded)

        // Then
        assertEquals(dto, decoded)
        assertEquals(1000, decoded.availableBalance)
        assertEquals(250, decoded.pendingBalance)
        assertEquals("GBP", decoded.currency)
        assertEquals(2, decoded.activity.size)
    }

    @Test
    fun `MerchantResponseDto handles empty activity list`() {
        // Given
        val jsonResponse = """
            {
                "availableBalance": 1000,
                "pendingBalance": 250,
                "currency": "GBP",
                "activity": []
            }
        """.trimIndent()

        // When
        val result =
            json.decodeFromString<MerchantResponseDto>(jsonResponse)

        // Then
        assertEquals(1000, result.availableBalance)
        assertEquals(250, result.pendingBalance)
        assertEquals("GBP", result.currency)
        assertEquals(emptyList<ActivityItemDto>(), result.activity)
    }
}
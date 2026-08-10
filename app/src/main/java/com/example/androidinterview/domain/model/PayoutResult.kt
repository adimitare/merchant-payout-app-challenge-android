package com.example.androidinterview.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PayoutResult(
    val id: String,
    val amount: Int,
    val currency: Currency,
    val iban: String,
    val status: PayoutStatus,
    val createdAt: String
)

enum class PayoutStatus {
    COMPLETED,
    FAILED,
    PENDING
}
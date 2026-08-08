package com.example.androidinterview.domain.model

enum class ActivityType {
    PAYOUT,
    DEPOSIT,
    REFUND,
    FEE
}

enum class ActivityStatus {
    COMPLETED,
    PENDING,
    PROCESSING,
    FAILED
}

data class ActivityItem(
    val id: String,
    val type: String,
    val amount: Int,
    val currency: Currency,
    val date: String,
    val description: String,
    val status: String
)
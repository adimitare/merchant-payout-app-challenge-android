package com.example.androidinterview.data.mapper

import com.example.androidinterview.data.remote.dto.ActivityItemDto
import com.example.androidinterview.data.remote.dto.CreatePayoutResponseDto
import com.example.androidinterview.data.remote.dto.MerchantResponseDto
import com.example.androidinterview.domain.model.ActivityItem
import com.example.androidinterview.domain.model.ActivityStatus
import com.example.androidinterview.domain.model.ActivityType
import com.example.androidinterview.domain.model.Currency
import com.example.androidinterview.domain.model.Merchant
import com.example.androidinterview.domain.model.PayoutResult
import com.example.androidinterview.domain.model.PayoutStatus
import com.example.androidinterview.util.toFriendlyStatus
import com.example.androidinterview.util.toFriendlyType

fun MerchantResponseDto.toDomain(): Merchant {
    return Merchant(
        availableBalance = availableBalance,
        pendingBalance = pendingBalance,
        currency = currency.toCurrency(),
        activityItem = activity.map { it.toDomain() }
    )
}

private fun ActivityItemDto.toDomain(): ActivityItem {
    return ActivityItem(
        id = id,
        type = ActivityType.valueOf(type.uppercase()).toFriendlyType(),
        amount = amount,
        currency = currency.toCurrency(),
        date = date,
        description = description,
        status = ActivityStatus.valueOf(status.uppercase()).toFriendlyStatus()
    )
}

fun CreatePayoutResponseDto.toDomain(): PayoutResult {
    return PayoutResult(
        id = id,
        amount = amount,
        currency = currency.toCurrency(),
        iban = iban,
        status = when (status.uppercase()) {
            "COMPLETED" -> PayoutStatus.COMPLETED
            "FAILED" -> PayoutStatus.FAILED
            else -> PayoutStatus.PENDING
        },
        createdAt = createdAt
    )
}

private fun String.toCurrency(): Currency {
    return Currency.valueOf(this.uppercase())
}
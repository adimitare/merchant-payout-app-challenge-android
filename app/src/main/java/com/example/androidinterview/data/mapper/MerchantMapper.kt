package com.example.androidinterview.data.mapper

import com.example.androidinterview.data.remote.dto.ActivityItemDto
import com.example.androidinterview.data.remote.dto.MerchantResponseDto
import com.example.androidinterview.domain.model.*

fun MerchantResponseDto.toDomain(): Merchant {
    return Merchant(
        availableBalance = availableBalance,
        pendingBalance = pendingBalance,
        currency = currency.toCurrency(),
        activity = activity.map { it.toDomain() }
    )
}

private fun ActivityItemDto.toDomain(): Activity {
    return Activity(
        id = id,
        type = ActivityType.valueOf(type.uppercase()),
        amount = amount,
        currency = currency.toCurrency(),
        date = date,
        description = description,
        status = ActivityStatus.valueOf(status.uppercase())
    )
}

private fun String.toCurrency(): Currency {
    return Currency.valueOf(this.uppercase())
}
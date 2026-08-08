package com.example.androidinterview.data.local.mapper

import com.example.androidinterview.data.local.entity.ActivityEntity
import com.example.androidinterview.data.remote.dto.ActivityItemDto
import com.example.androidinterview.domain.model.ActivityItem
import com.example.androidinterview.domain.model.ActivityStatus
import com.example.androidinterview.domain.model.ActivityType
import com.example.androidinterview.domain.model.Currency
import com.example.androidinterview.util.toFriendlyStatus
import com.example.androidinterview.util.toFriendlyType

fun ActivityEntity.toDomain(): ActivityItem {
    return ActivityItem(
        id = id,
        type = ActivityType.valueOf(type.uppercase()).toFriendlyType(),
        amount = amount,
        currency = Currency.valueOf(currency.uppercase()),
        date = date,
        description = description,
        status = ActivityStatus.valueOf(status.uppercase()).toFriendlyStatus()
    )
}

fun ActivityItemDto.toEntity(): ActivityEntity {
    return ActivityEntity(
        id = id,
        type = type,
        amount = amount,
        currency = currency,
        date = date,
        description = description,
        status = status
    )
}
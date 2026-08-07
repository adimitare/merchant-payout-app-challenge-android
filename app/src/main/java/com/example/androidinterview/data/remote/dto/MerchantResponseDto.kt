package com.example.androidinterview.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MerchantResponseDto(
    val availableBalance: Int,
    val pendingBalance: Int,
    val currency: String,
    val activity: List<ActivityItemDto>
)
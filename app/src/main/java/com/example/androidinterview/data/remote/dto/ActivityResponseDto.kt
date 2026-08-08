package com.example.androidinterview.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActivityResponseDto(
    val items: List<ActivityItemDto>,
    @SerialName("next_cursor")
    val nextCursor: String?,
    @SerialName("has_more")
    val hasMore: Boolean
)
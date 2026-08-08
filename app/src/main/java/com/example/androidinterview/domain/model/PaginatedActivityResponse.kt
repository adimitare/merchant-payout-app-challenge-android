package com.example.androidinterview.domain.model

data class PaginatedActivityResponse(
    val items: List<ActivityItem>,
    val next_cursor: String?,
    val has_more: Boolean,
)
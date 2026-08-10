package com.example.androidinterview.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreatePayoutResponseDto(
    val id: String,
    val status: String,
    val amount: Int,
    val currency: String,
    val iban: String,

    @SerialName("created_at")
    val createdAt: String
)
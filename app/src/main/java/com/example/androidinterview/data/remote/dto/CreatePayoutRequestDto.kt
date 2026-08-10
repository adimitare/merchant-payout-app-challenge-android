package com.example.androidinterview.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreatePayoutRequestDto(
    val amount: Int,
    val currency: String,
    val iban: String
)
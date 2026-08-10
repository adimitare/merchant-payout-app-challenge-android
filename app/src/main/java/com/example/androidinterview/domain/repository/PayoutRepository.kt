package com.example.androidinterview.domain.repository

import com.example.androidinterview.domain.model.PayoutResult

interface PayoutRepository {
    suspend fun createPayout(
        amount: Int,
        currency: String,
        iban: String
    ): PayoutResult
}
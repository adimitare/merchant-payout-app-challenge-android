package com.example.androidinterview.domain.usecase

import com.example.androidinterview.domain.model.PayoutResult
import com.example.androidinterview.domain.repository.PayoutRepository
import javax.inject.Inject

class SubmitPayoutUseCase @Inject constructor(
    private val repository: PayoutRepository
) {
    suspend operator fun invoke(
        amount: Int,
        currency: String,
        iban: String
    ): PayoutResult {
        return repository.createPayout(
            amount = amount,
            currency = currency,
            iban = iban
        )
    }
}
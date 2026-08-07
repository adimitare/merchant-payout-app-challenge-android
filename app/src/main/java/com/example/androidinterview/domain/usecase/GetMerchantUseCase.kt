package com.example.androidinterview.domain.usecase

import com.example.androidinterview.domain.model.Merchant
import com.example.androidinterview.domain.repository.MerchantRepository
import javax.inject.Inject

class GetMerchantUseCase @Inject constructor(
    private val repository: MerchantRepository
) {
    suspend operator fun invoke(): Merchant {
        return repository.getMerchant()
    }
}
package com.example.androidinterview.domain.repository

import com.example.androidinterview.domain.model.Merchant

interface MerchantRepository {
    suspend fun getMerchant(): Merchant
}
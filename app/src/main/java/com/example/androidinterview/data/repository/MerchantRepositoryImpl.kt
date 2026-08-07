package com.example.androidinterview.data.repository

import com.example.androidinterview.data.mapper.toDomain
import com.example.androidinterview.data.remote.MerchantApi
import com.example.androidinterview.domain.model.Merchant
import com.example.androidinterview.domain.repository.MerchantRepository
import javax.inject.Inject

class MerchantRepositoryImpl @Inject constructor(
    private val api: MerchantApi
): MerchantRepository {

    override suspend fun getMerchant(): Merchant {
        return api.getMerchant()
            .toDomain()
    }
}
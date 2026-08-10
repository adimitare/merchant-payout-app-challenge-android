package com.example.androidinterview.data.repository

import com.example.androidinterview.data.remote.MerchantApi
import com.example.androidinterview.domain.repository.DeviceRepository
import javax.inject.Inject

class DeviceRepositoryImpl @Inject constructor(
    private val api: MerchantApi
) : DeviceRepository {

    override suspend fun getDeviceId(): String {
        return api.getDevice().deviceId
    }
}
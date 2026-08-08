package com.example.androidinterview.data.remote

import com.example.androidinterview.data.remote.dto.ActivityResponseDto
import com.example.androidinterview.data.remote.dto.DeviceDto
import com.example.androidinterview.data.remote.dto.MerchantResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface MerchantApi {
    @GET("api/merchant")
    suspend fun getMerchant(): MerchantResponseDto

    @GET("api/merchant/activity")
    suspend fun getActivityResponse(
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int = 15
    ): ActivityResponseDto

    @GET("api/devices")
    suspend fun getDevice(): DeviceDto
}
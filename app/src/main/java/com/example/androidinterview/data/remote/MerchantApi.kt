package com.example.androidinterview.data.remote

import com.example.androidinterview.data.remote.dto.ActivityResponseDto
import com.example.androidinterview.data.remote.dto.CreatePayoutRequestDto
import com.example.androidinterview.data.remote.dto.CreatePayoutResponseDto
import com.example.androidinterview.data.remote.dto.DeviceDto
import com.example.androidinterview.data.remote.dto.MerchantResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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

    @POST("api/payouts")
    suspend fun createPayout(
        @Body request: CreatePayoutRequestDto
    ): CreatePayoutResponseDto
}
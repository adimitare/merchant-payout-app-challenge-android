package com.example.androidinterview.data.remote

import com.example.androidinterview.data.remote.dto.MerchantResponseDto
import retrofit2.http.GET

interface MerchantApi {
    @GET("api/merchant")
    suspend fun getMerchant(): MerchantResponseDto

}
package com.example.androidinterview.domain.repository

interface DeviceRepository {
    suspend fun getDeviceId(): String
}
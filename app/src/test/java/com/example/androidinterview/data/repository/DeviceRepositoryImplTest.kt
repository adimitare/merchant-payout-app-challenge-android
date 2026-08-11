package com.example.androidinterview.data.repository

import com.example.androidinterview.data.remote.MerchantApi
import com.example.androidinterview.data.remote.dto.DeviceDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DeviceRepositoryImplTest {
    private lateinit var api: MerchantApi
    private lateinit var repository: DeviceRepositoryImpl

    @Before
    fun setup() {
        api = mockk()
        repository = DeviceRepositoryImpl(
            api = api
        )
    }

    @Test
    fun `getDeviceId returns device id from API`() = runTest {
        // Given
        val expectedDeviceId = "device-123"
        coEvery {
            api.getDevice()
        } returns DeviceDto(
            deviceId = expectedDeviceId
        )
        // When
        val result = repository.getDeviceId()
        // Then
        assertEquals(
            expectedDeviceId,
            result
        )
        coVerify(exactly = 1) {
            api.getDevice()
        }
    }
}
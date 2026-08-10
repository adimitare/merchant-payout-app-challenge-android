package com.example.androidinterview.di

import android.content.Context
import com.example.androidinterview.data.repository.ActivityRepositoryImpl
import com.example.androidinterview.data.repository.DeviceRepositoryImpl
import com.example.androidinterview.data.repository.MerchantRepositoryImpl
import com.example.androidinterview.data.repository.PayoutRepositoryImpl
import com.example.androidinterview.domain.repository.ActivityRepository
import com.example.androidinterview.domain.repository.DeviceRepository
import com.example.androidinterview.domain.repository.MerchantRepository
import com.example.androidinterview.domain.repository.PayoutRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindMerchantRepository(
        implementation: MerchantRepositoryImpl
    ): MerchantRepository

    @Binds
    @Singleton
    abstract fun bindActivityRepository(
        implementation: ActivityRepositoryImpl
    ): ActivityRepository

    @Binds
    @Singleton
    abstract fun bindPayoutRepository(
        implementation: PayoutRepositoryImpl
    ): PayoutRepository

    @Binds
    @Singleton
    abstract fun provideDeviceRepository(
        implementation: DeviceRepositoryImpl
    ): DeviceRepository
}
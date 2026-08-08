package com.example.androidinterview.di

import com.example.androidinterview.data.repository.ActivityRepositoryImpl
import com.example.androidinterview.data.repository.MerchantRepositoryImpl
import com.example.androidinterview.domain.repository.ActivityRepository
import com.example.androidinterview.domain.repository.MerchantRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
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
}
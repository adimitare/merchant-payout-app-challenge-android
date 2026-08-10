package com.example.androidinterview.data.di

import com.example.androidinterview.data.biometric.AndroidBiometricAuthenticator
import com.example.androidinterview.domain.biometric.BiometricAuthenticator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BiometricModule {
    @Binds
    @Singleton
    abstract fun bindBiometricAuthenticator(
        implementation: AndroidBiometricAuthenticator
    ): BiometricAuthenticator
}
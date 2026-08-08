package com.example.androidinterview.di

import android.content.Context
import androidx.room.Room
import com.example.androidinterview.data.local.AppDatabase
import com.example.androidinterview.data.local.dao.ActivityDao
import com.example.androidinterview.data.local.dao.ActivityRemoteKeysDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "interview_database"
        ).build()
    }

    @Provides
    fun provideActivityDao(
        database: AppDatabase
    ): ActivityDao {
        return database.activityDao()
    }

    @Provides
    fun provideActivityRemoteKeysDao(
        database: AppDatabase
    ): ActivityRemoteKeysDao {
        return database.activityRemoteKeysDao()
    }
}
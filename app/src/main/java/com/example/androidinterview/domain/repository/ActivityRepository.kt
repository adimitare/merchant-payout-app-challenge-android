package com.example.androidinterview.domain.repository

import androidx.paging.PagingData
import com.example.androidinterview.domain.model.ActivityItem
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {
    fun getActivities(): Flow<PagingData<ActivityItem>>
}
package com.example.androidinterview.data.repository

import androidx.paging.PagingData
import androidx.paging.map
import com.example.androidinterview.data.local.mapper.toDomain
import com.example.androidinterview.data.paging.ActivityPagerFactory
import com.example.androidinterview.domain.model.ActivityItem
import com.example.androidinterview.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ActivityRepositoryImpl @Inject constructor(
    private val pagerFactory: ActivityPagerFactory
) : ActivityRepository {

    override fun getActivities(): Flow<PagingData<ActivityItem>> {
        return pagerFactory
            .create()
            .map { pagingData ->
                pagingData.map { entity ->
                    entity.toDomain()
                }
            }
    }
}
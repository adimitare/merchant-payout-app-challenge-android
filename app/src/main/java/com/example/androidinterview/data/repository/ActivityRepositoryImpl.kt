package com.example.androidinterview.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.androidinterview.data.local.AppDatabase
import com.example.androidinterview.data.local.dao.ActivityDao
import com.example.androidinterview.data.local.mapper.toDomain
import com.example.androidinterview.data.paging.ActivityRemoteMediator
import com.example.androidinterview.data.remote.MerchantApi
import com.example.androidinterview.domain.model.ActivityItem
import com.example.androidinterview.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ActivityRepositoryImpl @Inject constructor(
    private val api: MerchantApi,
    private val database: AppDatabase,
    private val activityDao: ActivityDao
) : ActivityRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getActivities(): Flow<PagingData<ActivityItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                initialLoadSize = 15,
                enablePlaceholders = false
            ),
            remoteMediator = ActivityRemoteMediator(
                api = api,
                database = database
            ),
            pagingSourceFactory = {
                activityDao.pagingSource()
            }
        ).flow.map { pagingData ->
            pagingData.map { entity ->
                entity.toDomain()
            }
        }
    }
}
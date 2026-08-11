package com.example.androidinterview.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.androidinterview.data.local.AppDatabase
import com.example.androidinterview.data.local.dao.ActivityDao
import com.example.androidinterview.data.local.entity.ActivityEntity
import com.example.androidinterview.data.remote.MerchantApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class ActivityPagerFactory @Inject constructor(
    private val api: MerchantApi,
    private val database: AppDatabase,
    private val activityDao: ActivityDao
) {

    fun create(): Flow<PagingData<ActivityEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = ActivityRemoteMediator(
                api = api,
                database = database
            ),
            pagingSourceFactory = {
                activityDao.pagingSource()
            }
        ).flow
    }

    private companion object {
        const val PAGE_SIZE = 15
    }
}
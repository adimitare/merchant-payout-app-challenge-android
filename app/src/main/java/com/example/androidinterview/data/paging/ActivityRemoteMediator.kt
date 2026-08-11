package com.example.androidinterview.data.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.androidinterview.data.local.AppDatabase
import com.example.androidinterview.data.local.entity.ActivityEntity
import com.example.androidinterview.data.local.entity.ActivityRemoteKeysEntity
import com.example.androidinterview.data.local.mapper.toEntity
import com.example.androidinterview.data.remote.MerchantApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class ActivityRemoteMediator(
    private val api: MerchantApi,
    private val database: AppDatabase
) : RemoteMediator<Int, ActivityEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ActivityEntity>
    ): MediatorResult = withContext(Dispatchers.IO) {

        val activityDao = database.activityDao()
        val remoteKeysDao = database.activityRemoteKeysDao()

        try {
            val cursor = when (loadType) {

                LoadType.REFRESH -> {
                    null
                }

                LoadType.PREPEND -> {
                    return@withContext MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                }

                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                        ?: return@withContext MediatorResult.Success(
                            endOfPaginationReached = true
                        )

                    val remoteKey =
                        remoteKeysDao.remoteKeys(lastItem.id)

                    remoteKey?.nextCursor
                        ?: return@withContext MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                }
            }

            val response = api.getActivityResponse(
                cursor = cursor,
                limit = state.config.pageSize
            )

            val entities = response.items.map { item ->
                item.toEntity()
            }

            database.withTransaction {

                if (loadType == LoadType.REFRESH) {
                    remoteKeysDao.clearRemoteKeys()
                    activityDao.clearAll()
                }

                if (entities.isNotEmpty()) {

                    val keys = entities.map { entity ->
                        ActivityRemoteKeysEntity(
                            activityId = entity.id,
                            nextCursor = response.nextCursor
                        )
                    }

                    remoteKeysDao.insertAll(keys)
                    activityDao.insertAll(entities)
                }
            }

            MediatorResult.Success(
                endOfPaginationReached = !response.hasMore
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)

        } catch (e: HttpException) {
            MediatorResult.Error(e)

        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private companion object {
        const val TAG = "ActivityRemoteMediator"
    }
}
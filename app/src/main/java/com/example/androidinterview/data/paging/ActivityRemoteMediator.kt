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

    private val activityDao = database.activityDao()
    private val remoteKeysDao = database.activityRemoteKeysDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ActivityEntity>
    ): MediatorResult = withContext(Dispatchers.IO) {

        Log.d(TAG, "========================================")
        Log.d(TAG, "LOAD START")
        Log.d(TAG, "loadType=$loadType")
        Log.d(TAG, "pageSize=${state.config.pageSize}")
        Log.d(TAG, "pages=${state.pages.size}")
        Log.d(TAG, "totalItems=${state.pages.sumOf { it.data.size }}")
        Log.d(TAG, "firstItem=${state.firstItemOrNull()?.id}")
        Log.d(TAG, "lastItem=${state.lastItemOrNull()?.id}")

        try {
            val cursor = when (loadType) {

                LoadType.REFRESH -> {
                    Log.d(TAG, "REFRESH -> cursor=null")
                    null
                }

                LoadType.PREPEND -> {
                    Log.d(
                        TAG,
                        "PREPEND -> API only supports forward pagination, " +
                                "returning endOfPaginationReached=true"
                    )

                    return@withContext MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                }

                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()

                    if (lastItem == null) {
                        Log.d(
                            TAG,
                            "APPEND -> lastItem=null, " +
                                    "returning endOfPaginationReached=true"
                        )

                        return@withContext MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                    }

                    Log.d(
                        TAG,
                        "APPEND -> lastItem=${lastItem.id}"
                    )

                    val remoteKey = remoteKeysDao.remoteKeys(lastItem.id)

                    Log.d(
                        TAG,
                        "APPEND -> remoteKey=$remoteKey"
                    )

                    val nextCursor = remoteKey?.nextCursor

                    if (nextCursor == null) {
                        Log.d(
                            TAG,
                            "APPEND -> nextCursor=null for ${lastItem.id}, " +
                                    "returning endOfPaginationReached=true"
                        )

                        return@withContext MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                    }

                    Log.d(
                        TAG,
                        "APPEND -> nextCursor=$nextCursor"
                    )

                    nextCursor
                }
            }

            Log.d(
                TAG,
                "API REQUEST -> loadType=$loadType, " +
                        "cursor=$cursor, " +
                        "limit=${state.config.pageSize}"
            )

            val response = api.getActivityResponse(
                cursor = cursor,
                limit = state.config.pageSize
            )

            Log.d(
                TAG,
                "API RESPONSE -> itemCount=${response.items.size}, " +
                        "nextCursor=${response.nextCursor}, " +
                        "hasMore=${response.hasMore}"
            )

            Log.d(
                TAG,
                "API RESPONSE -> itemIds=${response.items.map { it.id }}"
            )

            val entities = response.items.map { it.toEntity() }

            Log.d(
                TAG,
                "MAPPED ENTITIES -> count=${entities.size}, " +
                        "ids=${entities.map { it.id }}"
            )

            database.withTransaction {

                if (loadType == LoadType.REFRESH) {
                    Log.d(TAG, "TRANSACTION -> clearing remote keys")
                    remoteKeysDao.clearRemoteKeys()

                    Log.d(TAG, "TRANSACTION -> clearing activities")
                    activityDao.clearAll()
                }

                if (entities.isNotEmpty()) {

                    val keys = entities.map { item ->
                        ActivityRemoteKeysEntity(
                            activityId = item.id,
                            nextCursor = response.nextCursor
                        )
                    }

                    Log.d(
                        TAG,
                        "TRANSACTION -> inserting ${keys.size} remote keys"
                    )

                    Log.d(
                        TAG,
                        "TRANSACTION -> key mapping=" +
                                keys.map {
                                    "${it.activityId} -> ${it.nextCursor}"
                                }
                    )

                    remoteKeysDao.insertAll(keys)

                    Log.d(
                        TAG,
                        "TRANSACTION -> inserting ${entities.size} activities"
                    )

                    activityDao.insertAll(entities)
                } else {
                    Log.d(
                        TAG,
                        "TRANSACTION -> response contained no entities"
                    )
                }
            }

            val endOfPaginationReached = !response.hasMore

            Log.d(
                TAG,
                "LOAD SUCCESS -> " +
                        "loadType=$loadType, " +
                        "cursor=$cursor, " +
                        "received=${entities.size}, " +
                        "nextCursor=${response.nextCursor}, " +
                        "hasMore=${response.hasMore}, " +
                        "endOfPaginationReached=$endOfPaginationReached"
            )

            Log.d(TAG, "========================================")

            MediatorResult.Success(
                endOfPaginationReached = endOfPaginationReached
            )

        } catch (e: IOException) {

            Log.e(
                TAG,
                "NETWORK ERROR -> loadType=$loadType",
                e
            )

            Log.d(TAG, "========================================")

            MediatorResult.Error(e)

        } catch (e: HttpException) {

            Log.e(
                TAG,
                "HTTP ERROR -> loadType=$loadType, " +
                        "code=${e.code()}",
                e
            )

            Log.d(TAG, "========================================")

            MediatorResult.Error(e)

        } catch (e: Exception) {

            Log.e(
                TAG,
                "UNEXPECTED ERROR -> loadType=$loadType",
                e
            )

            Log.d(TAG, "========================================")

            MediatorResult.Error(e)
        }
    }

    private companion object {
        const val TAG = "ActivityRemoteMediator"
    }
}
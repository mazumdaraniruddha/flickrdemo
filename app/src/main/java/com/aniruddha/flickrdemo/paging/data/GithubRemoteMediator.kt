package com.aniruddha.flickrdemo.paging.data

import android.util.Log
import androidx.paging.*
import androidx.room.withTransaction
import com.aniruddha.flickrdemo.paging.api.ApiService
import com.aniruddha.flickrdemo.paging.api.Photo
import com.aniruddha.flickrdemo.paging.db.RepoDataBase
import com.aniruddha.flickrdemo.paging.model.RemoteKeys
import retrofit2.HttpException
import java.io.IOException
import java.io.InvalidObjectException

// Start page index as per the API
private const val STARTING_PAGE_INDEX = 1

@OptIn(ExperimentalPagingApi::class)
class GithubRemoteMediator(
        private val clientQuery: String,
        private val apiService: ApiService,
        private val repoDataBase: RepoDataBase
): RemoteMediator<Int, Photo>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Photo>): MediatorResult {
        Log.d(TAG, "CALL LOAD: QUERY: $clientQuery LOADTYPE: $loadType, PAGE_STATE: ${state.anchorPosition}")
        val page = when(loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextIndex?.minus(1) ?: STARTING_PAGE_INDEX
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                if (remoteKeys?.nextIndex == null) {
                    throw InvalidObjectException("Remote key should not be null for $loadType")
                }
                remoteKeys.nextIndex
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                if (remoteKeys == null) {
                    // The LoadType is PREPEND so some data was loaded before,
                    // so we should have been able to get remote keys
                    // If the remoteKeys are null, then we're an invalid state and we have a bug
                    throw InvalidObjectException("Remote key and the prevKey should not be null")
                }
                // If the previous key is null, then we can't request more data
                val prevKey = remoteKeys.prevIndex
                if (prevKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                remoteKeys.prevIndex
            }
        }
        val apiQuery = clientQuery
        return try {
            val response = apiService.searchRepos(apiQuery, page, state.config.pageSize)

            val repos = response.photos?.photo?.map {
                it.apply {
                    this.query = clientQuery
                }
            }
            val endOfPaginationReached = repos?.isEmpty()

            repoDataBase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    repoDataBase.photosDao().clearDB()
                    repoDataBase.remoteKeysDao().clearRemoteKeys()
                    Log.d(TAG, "CLEAR DB ON REFRESH:")
                }
                val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached == true) null else page + 1
                val keys = repos?.map {
                    val key = RemoteKeys(photoId = it.id, prevIndex = prevKey, nextIndex = nextKey)
                    Log.d(TAG, "GENERATE REMOTE KEYS: $key, PAGINATION ENDED? $endOfPaginationReached," +
                            " PAGE: $page")
                    key
                }
                keys?.let {
                    repoDataBase.remoteKeysDao().insertAll(keys)
                }
                repos?.let {
                    repoDataBase.photosDao().insertAll(repos)
                }
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached == true)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Photo>): RemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
                ?.let { photo ->
                    // Get the remote keys of the last item retrieved
                    val key = repoDataBase.remoteKeysDao().remoteKeysRepoId(photo.id)
                    Log.d(TAG, "KEY FOR LAST ITEM: $key")
                    key
                }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Photo>): RemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
                ?.let { photo ->
                    // Get the remote keys of the first items retrieved
                    val key = repoDataBase.remoteKeysDao().remoteKeysRepoId(photo.id)
                    Log.d(TAG, "KEY FOR FIRST ITEM: $key")
                    key
                }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
            state: PagingState<Int, Photo>
    ): RemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { photoId ->
                val key = repoDataBase.remoteKeysDao().remoteKeysRepoId(photoId)
                Log.d(TAG, "KEY CLOSEST TO CURR POST: $key")
                key
            }
        }
    }

    companion object {
        val TAG = GithubRemoteMediator::class.java.simpleName
    }
}
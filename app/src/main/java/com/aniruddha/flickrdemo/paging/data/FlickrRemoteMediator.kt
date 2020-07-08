package com.aniruddha.flickrdemo.paging.data

import androidx.paging.*
import androidx.room.withTransaction
import com.aniruddha.flickrdemo.paging.api.ApiService
import com.aniruddha.flickrdemo.paging.model.Photo
import com.aniruddha.flickrdemo.paging.db.FlickrDataBase
import com.aniruddha.flickrdemo.paging.model.RemoteKeys
import retrofit2.HttpException
import java.io.IOException
import java.io.InvalidObjectException

// Start page index as per the API
private const val STARTING_PAGE_INDEX = 1

@OptIn(ExperimentalPagingApi::class)
class FlickrRemoteMediator(
        private val clientQuery: String,
        private val apiService: ApiService,
        private val flickrDataBase: FlickrDataBase
) : RemoteMediator<Int, Photo>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Photo>): MediatorResult {
        val page = when (loadType) {
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
        val method = if (apiQuery.isNullOrEmpty())
            ApiService.METHOD_GET_RECENT
        else
            ApiService.METHOD_SEARCH_QUERY

        return try {
            val response = apiService.searchRepos(apiQuery,
                    page,
                    state.config.pageSize,
                    method)

            val repos = response.photos?.photo?.map {
                it.apply {
                    this.query = clientQuery
                }
            }
            val endOfPaginationReached = repos?.isEmpty()

            flickrDataBase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    flickrDataBase.photosDao().clearDB()
                    flickrDataBase.remoteKeysDao().clearRemoteKeys()
                }
                val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached == true) null else page + 1
                val keys = repos?.map {
                    RemoteKeys(photoId = it.id, prevIndex = prevKey, nextIndex = nextKey)
                }
                keys?.let {
                    flickrDataBase.remoteKeysDao().insertAll(keys)
                }
                repos?.let {
                    flickrDataBase.photosDao().insertAll(repos)
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
                    flickrDataBase.remoteKeysDao().remoteKeysRepoId(photo.id)
                }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Photo>): RemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
                ?.let { photo ->
                    // Get the remote keys of the first items retrieved
                    flickrDataBase.remoteKeysDao().remoteKeysRepoId(photo.id)
                }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
            state: PagingState<Int, Photo>
    ): RemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { photoId ->
                flickrDataBase.remoteKeysDao().remoteKeysRepoId(photoId)
            }
        }
    }

    companion object {
        val TAG = FlickrRemoteMediator::class.java.simpleName
    }
}
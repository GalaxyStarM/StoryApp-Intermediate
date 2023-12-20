package id.ac.unri.storyapp.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import id.ac.unri.storyapp.data.local.entity.RemoteKeys
import id.ac.unri.storyapp.data.local.entity.Story
import id.ac.unri.storyapp.data.local.room.StoryDatabase
import id.ac.unri.storyapp.data.remote.network.ApiService
import id.ac.unri.storyapp.utils.wrapEspressoIdlingResource

@ExperimentalPagingApi
class StoryRemoteMediator(
    private val database: StoryDatabase,
    private val apiService: ApiService,
    private val token: String
): RemoteMediator<Int, Story>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Story>
    ): MediatorResult {
         val page = when(loadType) {
             LoadType.REFRESH -> {
                 val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                 remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
             }
             LoadType.PREPEND -> {
                 val remoteKeys = getRemoteKeyForFirstItem(state)
                 val prevKey = remoteKeys?.prevKey
                     ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                 prevKey
             }
             LoadType.APPEND -> {
                 val remoteKeys = getRemoteKeysForLastItem(state)
                 val nextKey = remoteKeys?.nextKey
                     ?:return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                 nextKey
             }
         }

        wrapEspressoIdlingResource {
            try {
                val responseData = apiService.getStories(token, page, state.config.pageSize)
                val endOfPaginationReached = responseData.listStory?.isEmpty()

                database.withTransaction {
                    if(loadType == LoadType.REFRESH) {
                        database.remoteKeysDao().deleteRemoteKeys()
                        database.storyDao().deleteAll()
                    }

                    val prevKey = if(page == 1) null else page -1
                    val nextKey = if(endOfPaginationReached!!) null else page + 1
                    val keys = responseData.listStory.map {
                        RemoteKeys(id = it?.id!! , prevKey = prevKey, nextKey = nextKey)
                    }

                    //Save RemoteKeys information to database
                    database.remoteKeysDao().inserAll(keys)

                    responseData.listStory.forEach {
                        val story = Story(
                            it?.id!!,
                            it.name!!,
                            it.description!!,
                            it.createdAt!!,
                            it.photoUrl!!,
                            it.lon,
                            it.lat
                        )
                        database.storyDao().insertStory(story)
                    }
                }
                return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached!!)
            } catch (e: Exception) {
                return  MediatorResult.Error(e)
            }
        }
    }

    private suspend fun getRemoteKeysForLastItem(state: PagingState<Int, Story>): RemoteKeys? {
        return state.pages.lastOrNull() {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Story>): RemoteKeys? {
        return state.pages.firstOrNull() {
            it.data.isNotEmpty()
        }?.data?.firstOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Story>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }
}
package id.ac.unri.storyapp.data

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import id.ac.unri.storyapp.data.local.entity.Story
import id.ac.unri.storyapp.data.local.room.StoryDatabase
import id.ac.unri.storyapp.data.remote.StoryRemoteMediator
import id.ac.unri.storyapp.data.remote.network.ApiService
import id.ac.unri.storyapp.data.remote.response.AddNewStoryResponse
import id.ac.unri.storyapp.data.remote.response.StoryResponse
import id.ac.unri.storyapp.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@ExperimentalPagingApi
class StoryRepository @Inject constructor(
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase
) {
    fun getAllStories(token: String): LiveData<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
            ),
            remoteMediator = StoryRemoteMediator(
                storyDatabase,
                apiService,
                generateBearerToken(token)
            ),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStories()
            }
        ).liveData
    }

    suspend fun addStory(
        token : String,
        file : MultipartBody.Part,
        description : RequestBody,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ): Flow<Result<AddNewStoryResponse>> = flow {
        try {
            val bearerToken = generateBearerToken(token)
            val response = apiService.addStory(bearerToken, file, description, lat, lon)
            emit(Result.success(response))
            println("Response: $response")
        }catch (e: Exception) {
            println("Error: ${e.message}")
            emit(Result.failure(e))
        }
    }

    fun getAllStoriesWithLocation(token: String): Flow<Result<StoryResponse>> = flow {
        wrapEspressoIdlingResource {
            try {
                val bearerToken = generateBearerToken(token)
                val response = apiService.getStories(bearerToken, size = 30, location = 1)
                emit(Result.success(response))
            }catch (e: Exception) {
                e.printStackTrace()
                emit(Result.failure(e))
            }
        }
    }

    private fun generateBearerToken(token: String): String {
        return "Bearer $token"
    }
}
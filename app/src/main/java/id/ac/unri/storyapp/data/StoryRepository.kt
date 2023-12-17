package id.ac.unri.storyapp.data

import id.ac.unri.storyapp.data.remote.network.ApiService
import id.ac.unri.storyapp.data.remote.response.AddNewStoryResponse
import id.ac.unri.storyapp.data.remote.response.StoryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class StoryRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getAllStories(
        token:String,
        page: Int? = null,
        size: Int? = null
    ): Flow<Result<StoryResponse>> = flow {
        try {
            val bearerToken = generateBearerToken(token)
            val response = apiService.getStories(bearerToken, page, size)
            emit(Result.success(response))
        }catch (e: Exception){
            e.printStackTrace()
            emit(Result.failure(e))
        }

    }.flowOn(Dispatchers.IO)

    suspend fun addStory(
        token : String,
        file : MultipartBody.Part,
        description : RequestBody
    ): Flow<Result<AddNewStoryResponse>> = flow {
        try {
            val bearerToken = generateBearerToken(token)
            val response = apiService.addStory(bearerToken, file, description)
            emit(Result.success(response))
            println("Response: $response")
        }catch (e: Exception) {
            println("Error: ${e.message}")
            emit(Result.failure(e))
        }
    }

    private fun generateBearerToken(token: String): String {
        return "Bearer $token"
    }
}
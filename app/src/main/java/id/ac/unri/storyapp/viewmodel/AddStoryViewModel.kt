package id.ac.unri.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import dagger.hilt.android.lifecycle.HiltViewModel
import id.ac.unri.storyapp.data.AuthRepository
import id.ac.unri.storyapp.data.StoryRepository
import id.ac.unri.storyapp.data.remote.response.AddNewStoryResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
@ExperimentalPagingApi
@HiltViewModel
class AddStoryViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    private val authRepository: AuthRepository
): ViewModel() {

    fun getAuthToken(): Flow<String?> = authRepository.getAuthToken()

    suspend fun addStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): Flow<Result<AddNewStoryResponse>> =
        storyRepository.addStory(token, file, description, lat, lon)

}
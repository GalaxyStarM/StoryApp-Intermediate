package id.ac.unri.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import dagger.hilt.android.lifecycle.HiltViewModel
import id.ac.unri.storyapp.data.AuthRepository
import id.ac.unri.storyapp.data.StoryRepository
import id.ac.unri.storyapp.data.remote.response.StoryResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
@ExperimentalPagingApi
@HiltViewModel
class MapsViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    private val authRepository: AuthRepository
): ViewModel() {

    fun getAuthToken(): Flow<String?> = authRepository.getAuthToken()
    suspend fun getStoriesLocation(token: String): Flow<Result<StoryResponse>> = storyRepository.getAllStoriesWithLocation(token)
}
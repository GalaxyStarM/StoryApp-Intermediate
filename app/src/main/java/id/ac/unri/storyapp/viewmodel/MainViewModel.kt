package id.ac.unri.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.ac.unri.storyapp.data.AuthRepository
import id.ac.unri.storyapp.data.StoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val storyRepository: StoryRepository
): ViewModel(){

    fun saveAuthToken(token:String){
        viewModelScope.launch {
            authRepository.saveAuthToken(token)
        }
    }

    fun getAuthToken(): Flow<String?> = authRepository.getAuthToken()

    suspend fun getAllStories(
        token: String,
        page: Int? = null,
        size: Int? = null) =
        storyRepository.getAllStories(token, page, size)
}
package id.ac.unri.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import id.ac.unri.storyapp.data.AuthRepository
import id.ac.unri.storyapp.data.StoryRepository
import id.ac.unri.storyapp.data.local.entity.Story
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject
@ExperimentalPagingApi
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

    fun getAllStories(token: String): LiveData<PagingData<Story>> =
        storyRepository.getAllStories(token).cachedIn(viewModelScope)
}
package id.ac.unri.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.ac.unri.storyapp.data.AuthRepository
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel(){

    suspend fun register(name:String, email: String, password: String) = authRepository.userRegister(name, email, password)
}
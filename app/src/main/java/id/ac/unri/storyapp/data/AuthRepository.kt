package id.ac.unri.storyapp.data

import id.ac.unri.storyapp.data.local.data_source.AuthPreferencesDataSource
import id.ac.unri.storyapp.data.remote.network.ApiService
import id.ac.unri.storyapp.data.remote.response.LoginResponse
import id.ac.unri.storyapp.data.remote.response.RegisterResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val preferences: AuthPreferencesDataSource
){

    suspend fun userLogin(email:String, password: String): Flow<Result<LoginResponse>> = flow {
        try {
            val response = apiService.login(email, password)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun userRegister(name: String, email:String, password: String): Flow<Result<RegisterResponse>> = flow {
        try {
            val response = apiService.register(name, email, password)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun saveAuthToken(token:String) {
        preferences.saveAuthToken(token)
    }

    fun getAuthToken(): Flow<String?> = preferences.getAuthToken()

}
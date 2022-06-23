package com.example.finalprojectbinaracademy_secondhandapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.finalprojectbinaracademy_secondhandapp.data.local.datastore.DataStoreManager
import com.example.finalprojectbinaracademy_secondhandapp.data.remote.model.RegisterResponse
import com.example.finalprojectbinaracademy_secondhandapp.data.remote.model.UpdateProfileRequest
import com.example.finalprojectbinaracademy_secondhandapp.data.remote.repository.RemoteRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.parse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class EditProfileViewModel(
    private val remoteRepository: RemoteRepository,
    private val dataStore: DataStoreManager
): ViewModel() {
    private val _detailUser = MutableLiveData<RegisterResponse>()
    val detailUser : LiveData<RegisterResponse> get() = _detailUser

    private val _updateProfile = MutableLiveData<RegisterResponse>()
    val updateProfile: LiveData<RegisterResponse> get() = _updateProfile

    fun getAccessToken() : LiveData<String> {
        return dataStore.getAccessToken().asLiveData()
    }

    fun getUserByAccessToken(accessToken: String) {
        viewModelScope.launch {
            val getUser = remoteRepository.getUser(accessToken)
            val code = getUser.code()
            val body = getUser.body()

            if (code == 200) {
                body?.let {
                    _detailUser.postValue(it)
                }
            } else {
                Log.d("response error", "user register error")
            }
        }
    }

    fun updateProfile(accessToken: String,image:File,name: String,phone: String,address: String,city: String) {
        viewModelScope.launch {
            val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),image)
            val imageUpload = MultipartBody.Part.createFormData("image",image.name,requestFile)
            val name = RequestBody.create("text/plain".toMediaTypeOrNull(),name)
            val address = RequestBody.create("text/plain".toMediaTypeOrNull(),address)
            val phone = RequestBody.create("text/plain".toMediaTypeOrNull(),phone)
            val city = RequestBody.create("text/plain".toMediaTypeOrNull(),city)

            val updateProfile = remoteRepository.updateProfile(accessToken,name,phone,address,city,imageUpload)
            val code = updateProfile.code()
            val body = updateProfile.body()

            if (code == 200) {
                body?.let {
                    _updateProfile.postValue(it)
                }
            } else {
                _updateProfile.postValue(RegisterResponse("null","null","null","null","null",0,"null","null","null","null"))
            }
        }
    }

}
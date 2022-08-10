package com.example.finalprojectbinaracademy_secondhandapp.ui.viewmodel

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.example.finalprojectbinaracademy_secondhandapp.data.local.datastore.DataStoreManager
import com.example.finalprojectbinaracademy_secondhandapp.data.local.model.Banner
import com.example.finalprojectbinaracademy_secondhandapp.data.local.model.Product
import com.example.finalprojectbinaracademy_secondhandapp.data.remote.repository.RemoteRepository
import com.example.finalprojectbinaracademy_secondhandapp.utils.NetworkHelper
import com.example.finalprojectbinaracademy_secondhandapp.utils.Resource
import kotlinx.coroutines.launch

class HomeViewModel(
    private val remoteRepository: RemoteRepository,
    private val dataStore: DataStoreManager,
    private val networkHelper: NetworkHelper

) : ViewModel() {

    private val _getProduct = MutableLiveData<Resource<List<Product>>>()
    val getproduct: LiveData<Resource<List<Product>>>
        get() = _getProduct

    private val _getProductOffline = MutableLiveData<Resource<List<Product>>>()
    val gettProductOffline: LiveData<Resource<List<Product>>>
        get() = _getProductOffline

    private val _getBannerHome = MutableLiveData<Resource<List<Banner>>>()
    val getBannerHome: LiveData<Resource<List<Banner>>>
        get() = _getBannerHome

    init {
//        bannerHome()
    }

    fun bannerHome() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                try {
                    val response = remoteRepository.getBanner()
                    val codeResponse = response.code()

                    if (response.isSuccessful) {
                        _getBannerHome.postValue(Resource.success(response.body()))
                    } else {
                        val response = remoteRepository.getBannerOffline()
                        _getBannerHome.postValue(Resource.error("failed to get data banner",response))
                    }
                } catch (e: Exception) {
                    _getBannerHome.postValue(Resource.error(e.message.toString(),null))
                }
            } else {
                val response = remoteRepository.getBannerOffline()
                _getBannerHome.postValue(Resource.success(response))
            }
        }
    }

    fun getProductPaging() = remoteRepository.getProductPaging().cachedIn(viewModelScope)

    fun getProductOfflineAll() {
        viewModelScope.launch {
            _getProductOffline.postValue(Resource.loading(null))
            if (networkHelper.isNetworkConnected()) {
                try {
                    val params = HashMap<String,String>()
                    params["page"] = "1"
                    params["status"] = "available"
                    params["per_page"] = ""

                    val response = remoteRepository.getProductBoundResource(params)
                    if (response.isSuccessful) {
                        _getProductOffline.postValue(Resource.success(response.body()))
                    } else {
                        val response = remoteRepository.getProductOffline()
                        _getProductOffline.postValue(Resource.error("failed to get data from server",response))
                    }
                } catch (e: Exception) {
                    val response = remoteRepository.getProductOffline()
                    _getProductOffline.postValue(Resource.error("failed to get data from server",response))
                }
            } else {
                val response = remoteRepository.getProductOffline()
                _getProductOffline.postValue(Resource.success(response))
            }
        }
    }

    fun getProductOfflineCategory(categoryId : Int) {
        viewModelScope.launch {
            _getProductOffline.postValue(Resource.loading(null))
            if (networkHelper.isNetworkConnected()) {
                try {
                    val params = HashMap<String,String>()
                    params["page"] = "1"
                    params["per_page"] = ""
                    params["category_id"] = categoryId.toString()
                    val response = remoteRepository.getProductBoundResource(params)

                    if (response.isSuccessful) {
                        _getProductOffline.postValue(Resource.success(response.body()))
                    } else {
                        val response = remoteRepository.getProductOffline()
                        _getProductOffline.postValue(Resource.error("failed to get data from server",response))
                    }
                } catch (e: Exception) {
                    val response = remoteRepository.getProductOffline()
                    _getProductOffline.postValue(Resource.error("failed to get data from server",response))
                }
            } else {
                val response = remoteRepository.getProductOffline()
                _getProductOffline.postValue(Resource.success(response))
            }
        }
    }

    fun getSearchProduct(productName : String) {
        _getProduct.postValue(Resource.loading(null))
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val params = HashMap<String,String>()
                params["search"] = productName
                params["status"] = "available"
                try {
                    val product = remoteRepository.getProductBoundResource(params)
                    _getProduct.postValue(Resource.success(product.body()))
                } catch (e: Exception) {
                    _getProduct.postValue(Resource.error("failed to get data",null))
                }
            } else {
                _getProduct.postValue(Resource.error("please check your internet connection...",null))
            }
        }
    }

//    val products = remoteRepository.getProductBoundResource().asLiveData()

}
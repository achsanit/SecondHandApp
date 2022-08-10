package com.example.finalprojectbinaracademy_secondhandapp.ui.viewmodel

import android.util.Log
import androidx.datastore.dataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalprojectbinaracademy_secondhandapp.data.local.datastore.DataStoreManager
import com.example.finalprojectbinaracademy_secondhandapp.data.remote.model.GetResponseProductId
import com.example.finalprojectbinaracademy_secondhandapp.data.remote.model.PostBuyerOrderRequest
import com.example.finalprojectbinaracademy_secondhandapp.data.remote.model.PostBuyerOrderResponse
import com.example.finalprojectbinaracademy_secondhandapp.data.remote.repository.RemoteRepository
import com.example.finalprojectbinaracademy_secondhandapp.utils.NetworkHelper
import com.example.finalprojectbinaracademy_secondhandapp.utils.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BuyerDetailViewModel(
    private val remoteRepository: RemoteRepository,
    private val dataStore: DataStoreManager,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _getProductId = MutableLiveData<Resource<GetResponseProductId>>()
    val getproductId: LiveData<Resource<GetResponseProductId>>
        get() = _getProductId

    private val _postBuyerOrder = MutableLiveData<Resource<PostBuyerOrderResponse>>()
    val postBuyerOrder: LiveData<Resource<PostBuyerOrderResponse>>
        get() = _postBuyerOrder

    fun BuyerDetailProdukId(buyerId: Int) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                try {
                    val productId = remoteRepository.getBuyerProductId(buyerId)
                    if (productId.isSuccessful) {
                        _getProductId.postValue(Resource.success(productId.body()))
                    } else {
                        _getProductId.postValue(Resource.error("Failed to get detail product",null))
                    }
                } catch (e: Exception) {
                    _getProductId.postValue(Resource.error(e.message.toString(),null))
                }
            } else {
              _getProductId.postValue(Resource.error("please check your internet connection...",null))
            }
        }
    }

    fun PostBuyerOrder(request: PostBuyerOrderRequest){
        viewModelScope.launch {
            _postBuyerOrder.postValue(Resource.loading(null))
            if (networkHelper.isNetworkConnected()) {
                try {
                    dataStore.getAccessToken().collectLatest {
                        val postBuyerOrder = remoteRepository.postBuyerOrder(it,request)
                        if (postBuyerOrder.isSuccessful){
                            _postBuyerOrder.postValue(Resource.success(postBuyerOrder.body()))
                        } else {
                            if (postBuyerOrder.code() == 400) {
                                _postBuyerOrder.postValue(Resource.error("anda sudah order produk ini", null))
                            } else if (postBuyerOrder.code() == 403) {
                                _postBuyerOrder.postValue(Resource.error("silahkan login terlebih dahulu", null))
                            }
                        }
                    }
                } catch (e: Exception) {
                    _postBuyerOrder.postValue(Resource.error(e.message.toString(),null))
                }
            } else {
                _postBuyerOrder.postValue(Resource.error("please check your internet connection...", null))
            }
        }
    }
}
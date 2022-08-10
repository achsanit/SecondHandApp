package com.example.finalprojectbinaracademy_secondhandapp.ui.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.finalprojectbinaracademy_secondhandapp.data.local.datastore.DataStoreManager
import com.example.finalprojectbinaracademy_secondhandapp.data.local.db.LocalDao
import com.example.finalprojectbinaracademy_secondhandapp.data.local.db.LocalDaoHelper
import com.example.finalprojectbinaracademy_secondhandapp.data.remote.model.GetResponseProductId
import com.example.finalprojectbinaracademy_secondhandapp.data.remote.model.PostBuyerOrderRequest
import com.example.finalprojectbinaracademy_secondhandapp.data.remote.model.PostBuyerOrderResponse
import com.example.finalprojectbinaracademy_secondhandapp.data.remote.model.User
import com.example.finalprojectbinaracademy_secondhandapp.data.remote.repository.RemoteRepository
import com.example.finalprojectbinaracademy_secondhandapp.data.remote.service.ApiHelper
import com.example.finalprojectbinaracademy_secondhandapp.data.remote.service.ApiService
import com.example.finalprojectbinaracademy_secondhandapp.utils.NetworkHelper
import com.example.finalprojectbinaracademy_secondhandapp.utils.Resource
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Response

@RunWith(AndroidJUnit4::class)
class BuyerDetailViewModelTest {

    private lateinit var localDaoHelper: LocalDaoHelper
    private lateinit var apiHelper: ApiHelper
    private lateinit var remoteRepository: RemoteRepository
    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var networkHelper: NetworkHelper

    private lateinit var buyerDetailViewModel: BuyerDetailViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var apiService: ApiService
    @Mock
    private lateinit var localDao: LocalDao
    @Mock
    private lateinit var productByIdObserver: Observer<Resource<GetResponseProductId>>
    @Mock
    private lateinit var postOrderProductObserver: Observer<Resource<PostBuyerOrderResponse>>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        val context = ApplicationProvider.getApplicationContext<Context>()
        localDaoHelper = LocalDaoHelper(localDao)
        apiHelper = ApiHelper(apiService)
        remoteRepository = RemoteRepository(apiHelper,localDaoHelper)
        dataStoreManager = DataStoreManager(context)
        networkHelper = NetworkHelper(context)

        buyerDetailViewModel = BuyerDetailViewModel(remoteRepository,dataStoreManager,networkHelper)

    }

    @Test
    fun getProductByIdSuccess() {
        runBlocking {
            Mockito.`when`(apiService.getBuyerProductId(0)).thenReturn(Response.success(
                GetResponseProductId(
                0, emptyList(),"","",0,"","","","","",
                    "", User("","","","",0,"",""),0)
            ))

            buyerDetailViewModel.BuyerDetailProdukId(0)
            buyerDetailViewModel.getproductId.observeForever(productByIdObserver)
            Mockito.verify(apiService).getBuyerProductId(0)
            Mockito.verify(productByIdObserver).onChanged(Resource.success(GetResponseProductId(
                0, emptyList(),"","",0,"","","","","",
                "", User("","","","",0,"",""),0)
            ))
            buyerDetailViewModel.getproductId.removeObserver(productByIdObserver)
        }
    }

    @Test
    fun getProductByIdFailed() {
        runBlocking {
            Mockito.`when`(apiService.getBuyerProductId(0))
                .thenReturn(Response.error(401,"error response".toResponseBody("text/plain".toMediaTypeOrNull())))

            buyerDetailViewModel.BuyerDetailProdukId(0)
            buyerDetailViewModel.getproductId.observeForever(productByIdObserver)
            Mockito.verify(apiService).getBuyerProductId(0)
            Mockito.verify(productByIdObserver).onChanged(Resource.error("Failed to get detail product",null))
            buyerDetailViewModel.getproductId.removeObserver(productByIdObserver)
        }
    }

    @Test
    fun postOrderSuccess() {
        val request = PostBuyerOrderRequest(
            "0","0000"
        )

        runBlocking {
            dataStoreManager.setAccessToken("accessToken")
            Mockito.`when`(apiService.postBuyerOrder("accessToken",request))
                .thenReturn(Response.success(PostBuyerOrderResponse(
                    0,0,"",0,"",0,
                    0,"","","",""))
                )

            buyerDetailViewModel.PostBuyerOrder(request)
            buyerDetailViewModel.postBuyerOrder.observeForever(postOrderProductObserver)
            Mockito.verify(apiService).postBuyerOrder("accessToken",request)
            Mockito.verify(postOrderProductObserver).onChanged(Resource.success(PostBuyerOrderResponse(
                0,0,"",0,"",0,
                0,"","","",""))
            )
            buyerDetailViewModel.postBuyerOrder.removeObserver(postOrderProductObserver)
        }
    }

    @Test
    fun postOrderFailed() {
        val request = PostBuyerOrderRequest(
            "0","0000"
        )
        runBlocking {
            dataStoreManager.setAccessToken("accessToken")
            Mockito.`when`(apiService.postBuyerOrder("accessToken",request))
                .thenReturn(Response.error(400,"error response".toResponseBody("text/plain".toMediaTypeOrNull())))

            buyerDetailViewModel.PostBuyerOrder(request)
            buyerDetailViewModel.postBuyerOrder.observeForever(postOrderProductObserver)
            Mockito.verify(apiService).postBuyerOrder("accessToken",request)
            Mockito.verify(postOrderProductObserver).onChanged(Resource.error("error PostBuyerOrder", null))
            buyerDetailViewModel.postBuyerOrder.removeObserver(postOrderProductObserver)
        }
    }

    @After
    fun tearDown() {
    }
}
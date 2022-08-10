package com.example.finalprojectbinaracademy_secondhandapp.data.remote.repository

import android.util.Log
import androidx.paging.*
import com.example.finalprojectbinaracademy_secondhandapp.data.local.db.LocalDaoHelper
import com.example.finalprojectbinaracademy_secondhandapp.data.local.model.*
import com.example.finalprojectbinaracademy_secondhandapp.data.local.model.Product
import com.example.finalprojectbinaracademy_secondhandapp.data.remote.model.*
import com.example.finalprojectbinaracademy_secondhandapp.data.remote.service.ApiHelper
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class RemoteRepository(
    private val apiHelper: ApiHelper,
    private val localDaoHelper: LocalDaoHelper
) {

    suspend fun registerUser(request: RegisterRequest): Response<RegisterResponse> {
        return apiHelper.registerUser(request)
    }

    suspend fun loginUser(request: LoginRequest): Response<LoginResponse> {
        return apiHelper.loginUser(request)
    }

    suspend fun getUser(accessToken: String): Response<RegisterResponse> {
        return apiHelper.getUser(accessToken)
    }

    suspend fun updateProfile(
        accessToken: String,
        name: RequestBody,
        phone: RequestBody,
        address: RequestBody,
        city: RequestBody,
        image: MultipartBody.Part
    ): Response<RegisterResponse> {
        return apiHelper.updateProfile(accessToken, name, phone, address, city, image)
    }

    suspend fun changePassword(
        accessToken: String,
        currentPass: RequestBody,
        newPass: RequestBody,
        confirmPass: RequestBody
    ): Response<ChangePasswordResponse> {
        return apiHelper.changePassword(accessToken, currentPass, newPass, confirmPass)
    }

    suspend fun getNotification(accessToken: String): List<Notification> {
        val response = apiHelper.getNotification(accessToken)
        Log.d("1244",response.toString())
        localDaoHelper.deleteInsertNotif(response)
        return response
    }

    fun getNotifOffline(): List<Notification> {
        return localDaoHelper.getAllNotification()
    }

    suspend fun readNotification(accessToken: String, id: Int): Response<ReadNotificationResponse> {
        return apiHelper.readNotification(accessToken, id)
    }

    suspend fun getBuyerProduct(page: Int): Response<List<GetProductResponseItem>> {
        val parameters = HashMap<String,String>()
        parameters["page"] = page.toString()
        parameters["per_page"] = "20"
        return apiHelper.getBuyerProduct(parameters)
    }

    suspend fun getBuyerProductSearch(parameters: HashMap<String,String>): Response<List<GetProductResponseItem>> {
        return apiHelper.getBuyerProduct(parameters)
    }

    suspend fun getBanner(): Response<List<Banner>> {
        val response = apiHelper.getBanner()
        response.body()?.let {
            localDaoHelper.deleteInsertBanner(it)
        }
        return response
    }

    fun getBannerOffline(): List<Banner> {
        return localDaoHelper.getALlBanner()
    }

    fun getProductPaging() = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = {ProductPageSource(apiHelper)}
    ).liveData

    suspend fun getProductBoundResource(params: HashMap<String,String>): Response<List<Product>> {
        val response = apiHelper.getProducBoundResource(params)
        val data = response.body()
        data?.let {
            localDaoHelper.deleteAndInsertData(it.take(40))
        }
        return response
    }

//    fun getProductBoundResource() = networkBoundResource(
//        query = { productDao.getAllProducts() },
//        fetch = {
//            val params = HashMap<String,String>()
//            params["page"] = "1"
//            params["per_page"] = "11"
//            delay(2000)
//            Log.d("GETTTTT","MASOOOKKK")
//            apiHelperImpl.getProducBoundResource(params)
//        },
//        saveFetchResult = {
//            db.withTransaction {
//                productDao.deleteAllProduct()
//                productDao.insertProduct(it.data)
//                Log.d("SAVEEEE",it.data.size.toString())
//            }
//        }
//    )

    fun getProductOffline(): List<Product> {
        return localDaoHelper.getAllProduct()
    }

    suspend fun getBuyerProductId(buyerId: Int): Response<GetResponseProductId> {
        return apiHelper.getBuyerProductId(buyerId)
    }

    suspend fun getCategory(): Response<CategoryResponse> {
        return apiHelper.getCategory()
    }

    suspend fun getCategoryById(id: Int): Response<CategoryResponseItem> {
        return apiHelper.getCategoryById(id)
    }

    suspend fun postProduct(
        accessToken: String,
        name: RequestBody,
        description: RequestBody,
        basePrice: RequestBody,
        categoryId: RequestBody,
        location: RequestBody,
        productImage: MultipartBody.Part
    ): Response<PostProductResponse> {
        return apiHelper.postProduct(
            accessToken,
            name,
            description,
            basePrice,
            categoryId,
            location,
            productImage
        )
    }

    suspend fun patchStatusProduct(
        accessToken: String,
        idOrder: Int,
        status: RequestBody
    ): Response<PostProductResponse> {
        return apiHelper.patchStatusProduct(accessToken, idOrder, status)
    }

    suspend fun getSellerProduct(accessToken: String): Response<List<SellerProduct>> {
        val response = apiHelper.sellerGetProduct(accessToken)
        response.body()?.let {
            localDaoHelper.deleteAndInsertDataSeller(it)
        }
        return response
    }

    fun getSellerProductOffline(): List<SellerProduct> {
        return localDaoHelper.getAllProductSeller()
    }

    suspend fun getSellerOrder(
        accessToken: String,
        status: String
    ): Response<List<SellerOrder>> {
        val response = apiHelper.getSellerOrder(accessToken,status)
        if (response.isSuccessful) {
            response.body()?.let {
                localDaoHelper.deleteAndInsertOrderSeller(it)
            }
        }
        return response
    }

    fun getSellerOrderOffline(): List<SellerOrder> {
        return localDaoHelper.getAllOrderSeller()
    }

    suspend fun getSellerOrderId(
        accessToken: String,
        idOrder: Int
    ): Response<GetSellerOrderResponseItem> {
        return apiHelper.getSellerOrderId(accessToken, idOrder)
    }

    suspend fun patchOrder(
        accessToken: String,
        idOrder: Int,
        status: RequestBody
    ): Response<PatchOrderResponse> {
        return apiHelper.patchOrder(accessToken, idOrder, status)
    }
    suspend fun postBuyerOrder(
        accessToken: String,
        request: PostBuyerOrderRequest
    ) : Response<PostBuyerOrderResponse>{
        return apiHelper.postBuyerOrder(accessToken, request)
    }

}
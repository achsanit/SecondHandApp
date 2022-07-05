package com.example.finalprojectbinaracademy_secondhandapp.data.remote.service

import com.example.finalprojectbinaracademy_secondhandapp.data.remote.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    //register
    @POST("auth/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>

    //login
    @POST("auth/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>

    //get detail user
    @GET("auth/user")
    suspend fun getUser(
        @Header("access_token") accessToken: String,
    ): Response<RegisterResponse>

    //update profile
    @Multipart
    @PUT("auth/user")
    suspend fun updateUserProfile(
        @Header("access_token") accessToken: String,
        @Part imageProfile: MultipartBody.Part,
        @Part("full_name") name: RequestBody,
        @Part("phone_number") phone: RequestBody,
        @Part("address") address: RequestBody,
        @Part("city") city: RequestBody
    ): Response<RegisterResponse>

    //notification
    @GET("notification")
    suspend fun getNotification(
        @Header("access_token") accessToken: String,
    ): Response<NotificationResponse>

    //read notification
    @PATCH("notification/{id}")
    suspend fun readNotification(
        @Header("access_token") accessToken: String,
        @Path("id") idNotif: Int
    ): Response<ReadNotificationResponse>

    //Get product buyer
    @GET("buyer/product")
    suspend fun getBuyerProduct(): Response<GetProductResponse>

    //Get banner
    @GET("seller/banner")
    suspend fun getBanner(
        @Header("access_token") accessToken: String
    ): Response<BannerResponse>

    //Get product buyer {id}
    @GET("buyer/product/{id}")
    suspend fun getBuyerProductId(
        @Path("id")
        buyerId: Int
    ): Response<GetResponseProductId>

    // get category
    @GET("seller/category")
    suspend fun getCategory() : Response<CategoryResponse>

    //get category by id
    @GET("seller/category/{id}")
    suspend fun getCategoryById(
        @Path("id") id: Int
    ) : Response<CategoryResponseItem>

    //post product
    @Multipart
    @POST("seller/product")
    suspend fun sellerPostProduct(
        @Header("access_token") accessToken: String,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("base_price") basePrice: RequestBody,
        @Part("category_ids") categoryId: RequestBody,
        @Part("location") location: RequestBody,
        @Part productImage: MultipartBody.Part
    ): Response<PostProductResponse>
}


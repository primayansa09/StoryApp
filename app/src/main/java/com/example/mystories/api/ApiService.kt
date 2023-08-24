package com.example.mystories.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("register")
    fun signupUser(
        @Field("name") nama: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<UserResponse>

    @GET("stories")
    fun getStories(
        @Header("Authorization") token: String,
    ): Call<ListStoryResponse>

    @GET("stories")
    suspend fun getAllStories(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): ListStoryResponse

    @Multipart
    @POST("stories")
    fun setStories(
        @Header("Authorization") token: String?,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<UserResponse>

    @GET("stories")
    fun getLocation(
        @Header("Authorization") token: String,
        @Query("location") location : Int = 1,
    ): Call<ListStoryResponse>
}
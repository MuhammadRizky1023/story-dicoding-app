package com.example.storyapp.API

import com.example.storyapp.Model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ListStoryApiService {
    @Headers("Content-Type: application/json")
    @POST("register")
    fun createdAccountUser(@Body requestRegister: RequestRegister): Call<ResponseMessage>

    @POST("login")
    fun loginAccountUser(@Body requestLogin: LoginRequest): Call<LoginResponse>

    @GET("stories")
    suspend fun getListStoryPagingPage(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int,
        @Header("Authorization") token: String
    ): StoryResponseItem


    @GET("stories")
    fun getMapUser(
        @Header("Authorization") token: String,
        @Query("location") location: Int
    ): Call<StoryResponseItem>


    @Multipart
    @POST("stories")
    fun uploadListStoryUser(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float?,
        @Part("lon") lon: Float?,
        @Header("Authorization") token: String
    ): Call<ResponseMessage>

}
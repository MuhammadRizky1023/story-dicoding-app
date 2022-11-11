package com.example.storyapp.Data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import com.example.storyapp.API.ListStoryApiConfig
import com.example.storyapp.API.ListStoryApiService
import com.example.storyapp.Model.*
import com.example.storyapp.database.ListStoryDatabase
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ListStoryRepository(
    private val listStoryDatabase: ListStoryDatabase,
    private val listStoryApiService: ListStoryApiService
) {

    private var _listStoryUser = MutableLiveData<List<ListStoryUserPage>>()
    var listStoryUser: LiveData<List<ListStoryUserPage>> = _listStoryUser

    private var _messages = MutableLiveData<String>()
    var messages: LiveData<String> = _messages

    private var _loadingIsActive = MutableLiveData<Boolean>()
    var loadingIsActive: LiveData<Boolean> = _loadingIsActive

    private var _accountUser = MutableLiveData<LoginResponse>()
    var accountUser: LiveData<LoginResponse> = _accountUser



    fun createAccountUser(requestRegister: RequestRegister) {
            _loadingIsActive.value = true
            val create = ListStoryApiConfig.getListStoryApiConfig().createdAccountUser(requestRegister)
            create.enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    _loadingIsActive.value = false
                    val responseMessage = response.body()
                    if (response.isSuccessful) {
                        _messages.value = responseMessage?.message.toString()
                    } else {
                        _messages.value = response.message()
                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, throwable: Throwable) {
                    _loadingIsActive.value = false
                    _messages.value = throwable.message.toString()
                }
            })
    }

    fun getLoginAccountUser(requestLogin: LoginRequest) {
            _loadingIsActive.value = true
            val get = ListStoryApiConfig.getListStoryApiConfig().loginAccountUser(requestLogin)
            get.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    _loadingIsActive.value = false
                    val loginResponse = response.body()

                    if (response.isSuccessful) {
                        _accountUser.value = loginResponse!!
                        _messages.value = "Login as ${_accountUser.value!!.loginResult.name}"
                    } else {
                        _messages.value = response.message()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, throwable: Throwable) {
                    _loadingIsActive.value = false
                    _messages.value = throwable.message.toString()
                }

            })
    }


    @ExperimentalPagingApi
    fun getListStoryUserPage(token: String): LiveData<PagingData<ListStoryUserPage>> {
        val listStoryPagingPage = Pager(
            config = PagingConfig(
                pageSize = 10
            ),

            remoteMediator = ListUserControllerMediator(listStoryDatabase,listStoryApiService, token),
            pagingSourceFactory = {
               listStoryDatabase.listStoryDao().getAllListStoryUserPage()
            }
        )
        return listStoryPagingPage.liveData


    }

    fun getMapUser(token: String) {
            _loadingIsActive.value = true
            val request = ListStoryApiConfig.getListStoryApiConfig().getMapUser("Bearer $token", 1)
            request.enqueue(object : Callback<StoryResponseItem> {
                override fun onResponse(
                    call: Call<StoryResponseItem>,
                    response: Response<StoryResponseItem>
                ) {
                    _loadingIsActive.value = false
                    val responseBody = response.body()
                    if (response.isSuccessful && responseBody?.message == "Stories fetched successfully") {
                        _listStoryUser.value = responseBody.listStory
                        _messages.value = responseBody.message.toString()

                    } else {
                        _messages.value = response.message()
                    }
                }

                override fun onFailure(call: Call<StoryResponseItem>, throwable: Throwable) {
                    _loadingIsActive.value = false
                    _messages.value = throwable.message.toString()
                }

            })
    }


    fun uploadStory(
        photo: MultipartBody.Part,
        description: RequestBody,
        token: String,
        latitude: Double?,
        longitude: Double?
    ) {
            _loadingIsActive.value = true
            val upload = ListStoryApiConfig.getListStoryApiConfig().uploadListStoryUser(
                photo, description, latitude?.toFloat(), longitude?.toFloat(),
                "Bearer $token"
            )
            upload.enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    _loadingIsActive.value = false
                    if (response.isSuccessful) {
                        val responseMessage = response.body()
                        if (responseMessage != null && !responseMessage.error) {
                            _messages.value = responseMessage.message
                        }
                    } else {
                        _messages.value = response.message()

                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, throwable: Throwable) {
                    _loadingIsActive.value = false
                    _messages.value = throwable.message.toString()
                }
            })
    }
}
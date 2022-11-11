package com.example.storyapp.Data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.storyapp.Adapter.ListStoryUserAdapter
import com.example.storyapp.Model.ListStoryUserPage
import com.example.storyapp.Model.LoginResponse
import com.example.storyapp.getAsynchronous
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import java.io.File


internal class ListStoryRepositoryTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = CoroutineRuleDispatcher()

    @Mock
    private lateinit var listStoryRepository: ListStoryRepository

    @Mock
    private var file = File("file")

    @Before
    fun setListRepositoryUser() {
        listStoryRepository = Mockito.mock(ListStoryRepository::class.java)
    }


    @Test
    fun `listStoryUser  return data  not  null`() {
        val dummyStories = DummyAccountUser.setDummyAccountListUserEntityDatabase()
        val expectedStories = MutableLiveData<List<ListStoryUserPage>>()
        expectedStories.value = dummyStories

        Mockito.`when`(listStoryRepository.listStoryUser).thenReturn(expectedStories)

        val actualStories = listStoryRepository.listStoryUser.getAsynchronous()

        Mockito.verify(listStoryRepository).listStoryUser

        Assert.assertNotNull(actualStories)
        Assert.assertEquals(expectedStories.value, actualStories)
        junit.framework.Assert.assertEquals(dummyStories.size, actualStories.size)
    }

    @Test
    fun `messages  return data  not  null`() {
        val messageAccount = MutableLiveData<String>()
        messageAccount.value = "Story Uploaded"

        Mockito.`when`(listStoryRepository.messages).thenReturn(messageAccount)

        val messages = listStoryRepository.messages.getAsynchronous()

        Mockito.verify(listStoryRepository).messages
        Assert.assertNotNull(messages)
        Assert.assertEquals(messageAccount.value, messages)
    }

    @Test
    fun `when loadingIsActive return data not be null`() {
        val loadingIsActive= MutableLiveData<Boolean>()
        loadingIsActive.value = true

        Mockito.`when`(listStoryRepository.loadingIsActive).thenReturn(loadingIsActive)

        val accountLoadingIsActive= listStoryRepository.loadingIsActive.getAsynchronous()

        Mockito.verify(listStoryRepository).loadingIsActive
        Assert.assertNotNull(accountLoadingIsActive)
        Assert.assertEquals(loadingIsActive.value, accountLoadingIsActive)
    }

    @Test
    fun `when accountUser  return not null`() {
        val dummyAccountUser = DummyAccountUser.setDummyAccountResponseLoginUser()

        val testingAccountUser = MutableLiveData<LoginResponse>()
        testingAccountUser.value = dummyAccountUser

        Mockito.`when`(listStoryRepository.accountUser).thenReturn(testingAccountUser)

        val accountUser = listStoryRepository.accountUser.getAsynchronous()

        Mockito.verify(listStoryRepository).accountUser
        Assert.assertNotNull(accountUser)
        Assert.assertEquals(testingAccountUser.value, accountUser)
    }



    @Test
    fun `verification createAccountUser is processing`() {
        val dummyAccountUser = DummyAccountUser.setDummyAccountRequestCreateUser()
        val createAccountResponse = MutableLiveData<String>()
        createAccountResponse.value = "User  Created"

        listStoryRepository.createAccountUser(dummyAccountUser)

        Mockito.verify(listStoryRepository).createAccountUser(dummyAccountUser)

        Mockito.`when`(listStoryRepository.messages).thenReturn(createAccountResponse)

        val verificationAccountUser = listStoryRepository.messages.getAsynchronous()

        Mockito.verify(listStoryRepository).messages
        Assert.assertNotNull(verificationAccountUser)
        Assert.assertEquals(createAccountResponse.value, verificationAccountUser)
    }

    @Test
    fun `verification getLoginAccountUser  is  processing`() {
        val dummyAccountUser = DummyAccountUser.setDummyAccountRequestLoginUser()
        val getDummyAccountUser = DummyAccountUser.setDummyAccountResponseLoginUser()

        val loginAccountUser = MutableLiveData<LoginResponse>()
        loginAccountUser.value = getDummyAccountUser

        listStoryRepository.getLoginAccountUser(dummyAccountUser)

        Mockito.verify(listStoryRepository).getLoginAccountUser(dummyAccountUser)

        Mockito.`when`(listStoryRepository.accountUser).thenReturn(loginAccountUser)

        val getLoginAccountUser = listStoryRepository.accountUser.getAsynchronous()

        Mockito.verify(listStoryRepository).accountUser
        Assert.assertNotNull(getDummyAccountUser)
        Assert.assertEquals(loginAccountUser.value, getLoginAccountUser)
    }


    @Suppress("DEPRECATION")
    @ExperimentalCoroutinesApi
    @ExperimentalPagingApi
    @Test
    fun `verification getListStoryUserPage function is  processing return  not null`() =
        mainCoroutineRule.runBlockingTest {
            val noopListStoryUpdatedCallBack = NoopListCallback()
            val dummyStory = DummyAccountUser.setDummyAccountListStoryUser()
            val data = PagedTestDataSources.snapshot(dummyStory)
            val story = MutableLiveData<PagingData<ListStoryUserPage>>()
            val myToken = "token"
            story.value = data

            Mockito.`when`(listStoryRepository.getListStoryUserPage(myToken)).thenReturn(story)

            val getListStoryUserPage = listStoryRepository.getListStoryUserPage(myToken).getAsynchronous()

            val asyncAccountPage = AsyncPagingDataDiffer(
                diffCallback = ListStoryUserAdapter.LIST_STORY_USER_CALLBACK,
                updateCallback = noopListStoryUpdatedCallBack,
                mainDispatcher = mainCoroutineRule.dispatcher,
                workerDispatcher = mainCoroutineRule.dispatcher,
            )
            asyncAccountPage.submitData(getListStoryUserPage)


            advanceUntilIdle()
            Mockito.verify(listStoryRepository).getListStoryUserPage(myToken)
            Assert.assertNotNull(asyncAccountPage.snapshot())
            Assert.assertEquals(dummyStory.size, asyncAccountPage.snapshot().size)
            Assert.assertEquals(dummyStory[0].name, asyncAccountPage.snapshot()[0]?.name)

        }


    @Test
    fun `verification getMapUser  is processing`() {
        val dummyAccountUser = DummyAccountUser.setDummyAccountListUserEntityDatabase()
        val getMapAccountUser= MutableLiveData<List<ListStoryUserPage>>()
        getMapAccountUser.value = dummyAccountUser

        val myToken = "token"
        listStoryRepository.getMapUser(myToken)
        Mockito.verify(listStoryRepository).getMapUser(myToken)

        Mockito.`when`(listStoryRepository.listStoryUser).thenReturn(getMapAccountUser)

        val getMapUser = listStoryRepository.listStoryUser.getAsynchronous()

        Mockito.verify(listStoryRepository).listStoryUser

        Assert.assertNotNull(getMapUser)
        Assert.assertEquals(getMapAccountUser.value, getMapUser)
        junit.framework.Assert.assertEquals(dummyAccountUser.size, getMapUser.size)
    }

    @Test
    fun `verification uploadStory is processing`() {
        val responseMessage= MutableLiveData<String>()
        responseMessage.value = "Uploaded Story was successfully"

        val requestUploadImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageUploadMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            "fileName",
            requestUploadImageFile
        )
        val description: RequestBody = "this is my description".toRequestBody("text/plain".toMediaType())
        val myToken = "token"
        val location = LatLng(0.0, 0.0)

        listStoryRepository.uploadStory(
            imageUploadMultipart,
            description,
            myToken,
            location.latitude,
            location.longitude
        )

        Mockito.verify(listStoryRepository).uploadStory(
            imageUploadMultipart,
            description,
            myToken,
            location.latitude,
            location.longitude
        )

        Mockito.`when`(listStoryRepository.messages).thenReturn(responseMessage)

        val uploadStory = listStoryRepository.messages.getAsynchronous()

        Mockito.verify(listStoryRepository).messages
        Assert.assertNotNull(uploadStory)
        Assert.assertEquals(responseMessage.value, uploadStory)
    }


    class NoopListCallback : ListUpdateCallback {
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
    }

    class PagedTestDataSources private constructor() :
        PagingSource<Int, LiveData<List<ListStoryUserPage>>>() {
        companion object {
            fun snapshot(listStory: List<ListStoryUserPage>): PagingData<ListStoryUserPage> {
                return PagingData.from(listStory)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryUserPage>>>): Int {
            return 0
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryUserPage>>> {
            return LoadResult.Page(emptyList(), 1, 2)
        }
    }
}
package com.example.storyapp.UI.ViewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.Data.DummyAccountUser
import com.example.storyapp.Model.ListStoryUserPage
import com.example.storyapp.getAsynchronous
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
internal class MapUserViewModelTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mapUserViewModel: MapUserViewModel
    private var dummyAccount= DummyAccountUser.setDummyAccountListUserEntityDatabase()

    @Before
    fun setMapUser() {
        mapUserViewModel = Mockito.mock(MapUserViewModel::class.java)
    }


    @Test
    fun `listStoryUser return  data not  null`() {
        val supposedStories = MutableLiveData<List<ListStoryUserPage>>()
        supposedStories.value = dummyAccount

        Mockito.`when`(mapUserViewModel.listStoryUser).thenReturn(supposedStories)

        val realListStories = mapUserViewModel.listStoryUser.getAsynchronous()

        Mockito.verify(mapUserViewModel).listStoryUser

        Assert.assertNotNull(realListStories)
        Assert.assertEquals(supposedStories.value, realListStories)
        Assert.assertEquals(dummyAccount.size, realListStories.size)
    }


    @Test
    fun `messages return data not null`() {
        val messageAccount = MutableLiveData<String>()
        messageAccount.value = "Stories fetched Successfully"

        Mockito.`when`(mapUserViewModel.message).thenReturn(messageAccount)

        val messages = mapUserViewModel.message.getAsynchronous()

        Mockito.verify(mapUserViewModel).message
        Assert.assertNotNull(messages)
        Assert.assertEquals(messageAccount.value,messages)
    }

    @Test
    fun `loadingIsActive return data not null`() {
        val loadingAccount = MutableLiveData<Boolean>()
       loadingAccount.value = true

        Mockito.`when`(mapUserViewModel.loadingIsActive).thenReturn(loadingAccount)

        val loadingIsActive= mapUserViewModel.loadingIsActive.getAsynchronous()

        Mockito.verify(mapUserViewModel).loadingIsActive
        Assert.assertNotNull(loadingIsActive)
        Assert.assertEquals(loadingAccount.value, loadingIsActive)
    }

    @Test
    fun `verification  getMapUser is processing`() {
        val mapUser = MutableLiveData<List<ListStoryUserPage>>()
        mapUser.value = dummyAccount

        val myToken = "token"
        mapUserViewModel.getMapUser(myToken)
        Mockito.verify(mapUserViewModel).getMapUser(myToken)

        Mockito.`when`(mapUserViewModel.listStoryUser).thenReturn(mapUser)

        val  getMapUser= mapUserViewModel.listStoryUser.getAsynchronous()

        Mockito.verify(mapUserViewModel).listStoryUser

        Assert.assertNotNull(getMapUser)
        Assert.assertEquals(mapUser.value, getMapUser)
        Assert.assertEquals(dummyAccount.size, getMapUser.size)
    }



}
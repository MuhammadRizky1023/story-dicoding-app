package com.example.storyapp.UI.ViewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
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
class StoryUserViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyUserViewModel: StoryUserViewModel

    private var token = "token"
    private var userName = "name"
    private var isLogin = true

    @Before
    fun setStoryUser() {
        storyUserViewModel = Mockito.mock(StoryUserViewModel::class.java)
    }

    @Test
    fun `getUserLoginAuth return  data not null`() {
        val expectedLoginState = MutableLiveData<Boolean>()
        expectedLoginState.value = isLogin

        Mockito.`when`(storyUserViewModel.getUserLoginAuth()).thenReturn(expectedLoginState)

        val actualLoginState = storyUserViewModel.getUserLoginAuth().getAsynchronous()

        Mockito.verify(storyUserViewModel).getUserLoginAuth()
        Assert.assertNotNull(actualLoginState)
        Assert.assertEquals(expectedLoginState.value, actualLoginState)
    }

    @Test
    fun `verification saveUserLoginAuth is  processing`() {
        storyUserViewModel.saveUserLoginAuth(isLogin)
        Mockito.verify(storyUserViewModel).saveUserLoginAuth(isLogin)
    }

    @Test
    fun `getTokenAuth return data  not  null`() {
        val tokenAccountAuth = MutableLiveData<String>()
        tokenAccountAuth.value = token

        Mockito.`when`(storyUserViewModel.getTokenAuth()).thenReturn(tokenAccountAuth)

        val myToken = storyUserViewModel.getTokenAuth().getAsynchronous()

        Mockito.verify(storyUserViewModel).getTokenAuth()
        Assert.assertNotNull(myToken)
        Assert.assertEquals(tokenAccountAuth.value, myToken)
    }

    @Test
    fun `verification saveTokenAuth  is processing`() {
        val token = "token"

        storyUserViewModel.saveTokenAuth(token)
        Mockito.verify(storyUserViewModel).saveTokenAuth(token)
    }

    @Test
    fun `getUserName return  data  not null`() {
        val accountUserName= MutableLiveData<String>()
        accountUserName.value = userName

        Mockito.`when`(storyUserViewModel.getUserName()).thenReturn(accountUserName)

        val userName = storyUserViewModel.getUserName().getAsynchronous()

        Mockito.verify(storyUserViewModel).getUserName()
        Assert.assertNotNull(userName)
        Assert.assertEquals(accountUserName.value, userName)
    }

    @Test
    fun `verification saveUserName is  processing`() {
        storyUserViewModel.saveUserName(userName)
        Mockito.verify(storyUserViewModel).saveUserName(userName)
    }

}
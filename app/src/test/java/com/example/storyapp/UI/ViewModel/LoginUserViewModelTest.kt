package com.example.storyapp.UI.ViewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.Data.DummyAccountUser
import com.example.storyapp.Model.LoginResponse
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
internal class LoginUserViewModelTest{
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var loginUserViewModel: LoginUserViewModel

    @Before
    fun setLoginAccount() {
        loginUserViewModel = Mockito.mock(LoginUserViewModel::class.java)
    }

    @Test
    fun `messages return not  null`() {
        val accountMessage = MutableLiveData<String>()
        accountMessage.value = "Login as"

        Mockito.`when`(loginUserViewModel.message).thenReturn(accountMessage)

        val messages = loginUserViewModel.message.getAsynchronous()

        Mockito.verify(loginUserViewModel).message
        Assert.assertNotNull(messages)
        Assert.assertEquals(accountMessage.value, messages)
    }

    @Test
    fun `loadingIsActive  return data not null`() {
        val loadingAccount = MutableLiveData<Boolean>()
        loadingAccount.value = true

        Mockito.`when`(loginUserViewModel.loadingIsActive).thenReturn(loadingAccount)

        val loadingIsActive = loginUserViewModel.loadingIsActive.getAsynchronous()

        Mockito.verify(loginUserViewModel).loadingIsActive
        Assert.assertNotNull(loadingIsActive)
        Assert.assertEquals(loadingAccount.value, loadingIsActive)
    }

    @Test
    fun `accountUser return data and not null`() {
        val dummyDataResponseLogin = DummyAccountUser.setDummyAccountResponseLoginUser()

        val expectedLogin = MutableLiveData<LoginResponse>()
        expectedLogin.value = dummyDataResponseLogin

        Mockito.`when`(loginUserViewModel.accountUser).thenReturn(expectedLogin)

        val realLoginResponse = loginUserViewModel.accountUser.getAsynchronous()

        Mockito.verify(loginUserViewModel).accountUser
        Assert.assertNotNull(realLoginResponse)
        Assert.assertEquals(expectedLogin.value, realLoginResponse)
    }

    @Test
    fun `verification getLoginAccountUser is processing`() {
        val dummyAccountUserRequestLogin = DummyAccountUser.setDummyAccountRequestLoginUser()
        val dummyResponseLogin = DummyAccountUser.setDummyAccountResponseLoginUser()

        val getAccount = MutableLiveData<LoginResponse>()
        getAccount.value = dummyResponseLogin

        loginUserViewModel.getLoginAccountUser(dummyAccountUserRequestLogin)

        Mockito.verify(loginUserViewModel).getLoginAccountUser(dummyAccountUserRequestLogin)

        Mockito.`when`(loginUserViewModel.accountUser).thenReturn(getAccount)

        val getLoginAccountUser = loginUserViewModel.accountUser.getAsynchronous()

        Mockito.verify(loginUserViewModel).accountUser
        Assert.assertNotNull(getAccount)
        Assert.assertEquals(getAccount.value, getLoginAccountUser)
    }
}
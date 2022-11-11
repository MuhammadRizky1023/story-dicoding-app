package com.example.storyapp.UI.ViewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.Data.DummyAccountUser
import com.example.storyapp.getAsynchronous
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito


internal class RegisterUserViewModelTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var registerUserViewModel: RegisterUserViewModel

    @Before
    fun setUpAccount() {
        registerUserViewModel= Mockito.mock(RegisterUserViewModel::class.java)
    }


    @Test
    fun `messages return data not null`() {
        val messageAccount  = MutableLiveData<String>()
        messageAccount.value = "User  Created"

        Mockito.`when`(registerUserViewModel.messages).thenReturn(messageAccount)

        val messages = registerUserViewModel.messages.getAsynchronous()

        Mockito.verify(registerUserViewModel).messages
        Assert.assertNotNull(messages)
        Assert.assertEquals(messageAccount.value, messages)
    }

    @Test
    fun `loadingIsActive return  not  null`() {
        val loadingAccount = MutableLiveData<Boolean>()
       loadingAccount.value = true

        Mockito.`when`(registerUserViewModel.loadingIsActive).thenReturn(loadingAccount)

        val loadingIsActive = registerUserViewModel.loadingIsActive.getAsynchronous()

        Mockito.verify(registerUserViewModel).loadingIsActive
        Assert.assertNotNull(loadingIsActive)
        Assert.assertEquals(loadingAccount.value, loadingIsActive)
    }

    @Test
    fun `verification createAccountUser is processing`() {
        val dummyAccountUser = DummyAccountUser.setDummyAccountRequestCreateUser()
        val createdAccountUser = MutableLiveData<String>()
        createdAccountUser.value = "User Created "

        registerUserViewModel.createAccountUser(dummyAccountUser)

        Mockito.verify(registerUserViewModel).createAccountUser(dummyAccountUser)

        Mockito.`when`(registerUserViewModel.messages).thenReturn(createdAccountUser)

        val testAccountUser = registerUserViewModel.messages.getAsynchronous()

        Mockito.verify(registerUserViewModel).messages
        Assert.assertNotNull(testAccountUser)
        Assert.assertEquals(createdAccountUser.value, testAccountUser)
    }
}
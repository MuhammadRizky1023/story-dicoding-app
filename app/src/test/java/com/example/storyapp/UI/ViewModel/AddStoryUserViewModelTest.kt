package com.example.storyapp.UI.ViewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.getAsynchronous
import com.google.android.gms.maps.model.LatLng
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


internal class AddStoryUserViewModelTest{
    @get:Rule
    var instantTaskExecutorRuleModel = InstantTaskExecutorRule()

    @Mock
    private lateinit var addStoryViewModel: AddListStoryUserViewModel
    private var upload = File("image")

    @Before
    fun setAddStory() {
        addStoryViewModel= Mockito.mock(AddListStoryUserViewModel::class.java)
    }

    @Test
    fun `messages return data not null`() {
        val messageAccount = MutableLiveData<String>()
        messageAccount.value = "Story Uploaded was successfully"

        Mockito.`when`(addStoryViewModel.messages).thenReturn(messageAccount)

        val messages= addStoryViewModel.messages.getAsynchronous()

        Mockito.verify(addStoryViewModel).messages
        Assert.assertNotNull(messages)
        Assert.assertEquals(messageAccount.value, messages)
    }

    @Test
    fun `loadingIsActive return data not null`() {
        val loadingAccount = MutableLiveData<Boolean>()
        loadingAccount.value = true

        Mockito.`when`(addStoryViewModel.loadingIsActive).thenReturn(loadingAccount)

        val loadingIsActive = addStoryViewModel.loadingIsActive.getAsynchronous()

        Mockito.verify(addStoryViewModel).loadingIsActive
        Assert.assertNotNull(loadingIsActive)
        Assert.assertEquals(loadingAccount.value, loadingIsActive)
    }

    @Test
    fun `verification uploadStory is processing`() {
        val expectedUploadMessage = MutableLiveData<String>()
        expectedUploadMessage.value = "Story Uploaded was Successfully"

        val requestUploadImageFile = upload.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "Photo",
            "image",
            requestUploadImageFile
        )
        val description: RequestBody = "my description is ....".toRequestBody("text/plain".toMediaType())
        val myToken = "token"
        val location = LatLng(0.0, 0.0)

        addStoryViewModel.uploadStory(imageMultipart, description, myToken, location.latitude, location.longitude)

        Mockito.verify(addStoryViewModel).uploadStory(
            imageMultipart,
            description,
            myToken,
            location.latitude,
            location.longitude
        )

        Mockito.`when`(addStoryViewModel.messages).thenReturn(expectedUploadMessage)

        val uploadStory = addStoryViewModel.messages.getAsynchronous()

        Mockito.verify(addStoryViewModel).messages
        Assert.assertNotNull(uploadStory)
        Assert.assertEquals(expectedUploadMessage.value, uploadStory)
    }
}
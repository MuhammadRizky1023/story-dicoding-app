package com.example.storyapp.UI.ViewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.storyaipp.ui.ViewModel.ListStoryUserViewModel
import com.example.storyapp.Adapter.ListStoryUserAdapter
import com.example.storyapp.Data.CoroutineRuleDispatcher
import com.example.storyapp.Data.DummyAccountUser
import com.example.storyapp.Model.ListStoryUserPage
import com.example.storyapp.getAsynchronous
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner


@OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
internal class ListStoryUserViewModelTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    var coroutineRuleDispatcher = CoroutineRuleDispatcher()

    @Mock
    private lateinit var listStoryViewModel: ListStoryUserViewModel

    @Before
    fun setListStoryUser() {
        listStoryViewModel = Mockito.mock(ListStoryUserViewModel::class.java)
    }

    @Test
    @Suppress("DEPRECATION")
    fun `verification getListStoryUserPage return not null`() =
        coroutineRuleDispatcher.runBlockingTest {
            val noopListUpdateCallback = NoopListCallback()
            val dummyAccountUser= DummyAccountUser.setDummyAccountListStoryUser()
            val listStoryUserPage = PagedTestDataSources.snapshot(dummyAccountUser)
            val pagingPage = MutableLiveData<PagingData<ListStoryUserPage>>()
            val myToken = "token"
            pagingPage.value = listStoryUserPage
            Mockito.`when`(listStoryViewModel.getListStoryUserPage(myToken)).thenReturn(pagingPage)
            val realDataListStory = listStoryViewModel.getListStoryUserPage(myToken).getAsynchronous()

            val differ = AsyncPagingDataDiffer(
                diffCallback = ListStoryUserAdapter.LIST_STORY_USER_CALLBACK,
                updateCallback = noopListUpdateCallback,
                mainDispatcher = coroutineRuleDispatcher.dispatcher,
                workerDispatcher = coroutineRuleDispatcher.dispatcher,
            )
            differ.submitData(realDataListStory)


            advanceUntilIdle()
            Mockito.verify(listStoryViewModel).getListStoryUserPage(myToken)
            Assert.assertNotNull(differ.snapshot())
            Assert.assertEquals(dummyAccountUser.size, differ.snapshot().size)
            Assert.assertEquals(dummyAccountUser[0].name, differ.snapshot()[0]?.name)

        }



    @Test
    fun `getListStoryUserPager is Empty return  not null`() =
        coroutineRuleDispatcher.runBlockingTest {
            val updateCallbackListStory = NoopListCallback()
            val data = PagedTestDataSources.snapshot(listOf())
            val listStoryUser = MutableLiveData<PagingData<ListStoryUserPage>>()
            val token = "token"
            listStoryUser.value = data
            Mockito.`when`(listStoryViewModel.getListStoryUserPage(token)).thenReturn(listStoryUser)
            val realStoryData = listStoryViewModel.getListStoryUserPage(token).getAsynchronous()

            val asyncPage= AsyncPagingDataDiffer(
                diffCallback = ListStoryUserAdapter.LIST_STORY_USER_CALLBACK,
                updateCallback = updateCallbackListStory,
                mainDispatcher = coroutineRuleDispatcher.dispatcher,
                workerDispatcher = coroutineRuleDispatcher.dispatcher,
            )
            asyncPage.submitData(realStoryData)


            advanceUntilIdle()
            Mockito.verify(listStoryViewModel).getListStoryUserPage(token)
            Assert.assertNotNull(asyncPage.snapshot())
            Assert.assertTrue(asyncPage.snapshot().isEmpty())
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
            fun snapshot(items: List<ListStoryUserPage>): PagingData<ListStoryUserPage> {
                return PagingData.from(items)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryUserPage>>>): Int {
            return 0
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryUserPage>>> {
            return LoadResult.Page(emptyList(), 1, 1)
        }
    }


}
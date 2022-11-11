package com.example.storyapp.Data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.storyapp.API.ListStoryApiService
import com.example.storyapp.Model.ListStoryUserPage
import com.example.storyapp.Model.UserControllerKey
import com.example.storyapp.database.ListStoryDatabase



@OptIn(ExperimentalPagingApi::class)
class ListUserControllerMediator(
    private val listStoryDatabase: ListStoryDatabase,
    private val listStoryApiService: ListStoryApiService,
     token: String
) : RemoteMediator<Int, ListStoryUserPage>() {
    var tokenAuth: String? = token

    private companion object {
        const val LIST_STORY_PER_PAGE = 1
        const val MY_LOCATION = 0

    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }


    override suspend fun load(
        loadingUserType: LoadType,
        listUser: PagingState<Int, ListStoryUserPage>,
    ): MediatorResult {
        val userPerPage = when (loadingUserType) {
            LoadType.REFRESH -> {
                val remoteKeys = getUserControllerKeyCloseListUserItem(listUser)
                remoteKeys?.nextKey?.minus(1) ?: LIST_STORY_PER_PAGE
            }
            LoadType.PREPEND -> {
                val controllerKey = getUserControllerKeyFirstListUserItem(listUser)
                val userControllerPrevKey = controllerKey?.prevKey
                    ?: return MediatorResult.Success( controllerKey!= null)
                userControllerPrevKey
            }
            LoadType.APPEND -> {
                val controllerKey= getUserControllerKeyEndListUserItem(listUser)
                val userControllerNextKey = controllerKey?.nextKey
                    ?: return MediatorResult.Success(controllerKey!= null)
                userControllerNextKey
            }
        }

        try {
            val userDataList: List<ListStoryUserPage>
            val userResponseListData =
                listStoryApiService.getListStoryPagingPage(userPerPage, listUser.config.pageSize, MY_LOCATION, "Bearer $tokenAuth")

            userDataList= userResponseListData.listStory

            val endingUserControllerPage = listUser.isEmpty()
            listStoryDatabase.withTransaction {
                if (loadingUserType == LoadType.REFRESH) {
                    listStoryDatabase.userControllerKeysDao().deleteAllUserControllerKey()
                    with(listStoryDatabase) { listStoryDao().deleteAllStoryUser() }
                }
                val userControllerPrevKey = if (userPerPage == 1) {
                    null
                } else{
                    userPerPage - 1
                }
                val userControllerNextKey = if (endingUserControllerPage){
                    null
                } else{
                  userPerPage + 1
                }
                val controllerKey= userDataList.map {
                    UserControllerKey(id = it.id, prevKey = userControllerPrevKey, nextKey = userControllerNextKey)
                }
                listStoryDatabase.userControllerKeysDao().addAllUserControllerKey(controllerKey)
                listStoryDatabase.listStoryDao().addListStory(userDataList)
            }
            return MediatorResult.Success(endOfPaginationReached = endingUserControllerPage)

        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getUserControllerKeyEndListUserItem(listUser: PagingState<Int, ListStoryUserPage>): UserControllerKey? {
        return listUser.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { listItemUser ->
            listStoryDatabase.userControllerKeysDao().getAllUserControllerById(listItemUser.id)
        }
    }

    private suspend fun getUserControllerKeyFirstListUserItem(listUser: PagingState<Int, ListStoryUserPage>): UserControllerKey? {
        return listUser.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { listStory ->
            listStoryDatabase.userControllerKeysDao().getAllUserControllerById(listStory.id)
        }
    }

    private suspend fun getUserControllerKeyCloseListUserItem(listUser: PagingState<Int, ListStoryUserPage>): UserControllerKey? {
        return listUser.anchorPosition?.let { listStoryUserPosition ->
            listUser.closestItemToPosition(listStoryUserPosition)?.id?.let { id ->
               listStoryDatabase.userControllerKeysDao().getAllUserControllerById(id)
            }
        }
    }
}

package com.example.storyapp.Data

import com.example.storyapp.Model.*


object DummyAccountUser {
    fun setDummyAccountListUserEntityDatabase(): List<ListStoryUserPage> {
        val listStoryUserPage = ArrayList<ListStoryUserPage>()
        for (item in 0..10) {
            val accountUser = ListStoryUserPage(
                " $item",
                "Muhammad Rizky",
                "My description is ...",
                "https://www.publicbooks.org/wp-content/uploads/2017/01/book-e1484158615982.jpg",
                "2022-11-09",
                null,
                null,
            )
            listStoryUserPage.add(accountUser)
        }
        return listStoryUserPage
    }

    fun setDummyAccountListStoryUser(): List<ListStoryUserPage> {
        val listStoryUserPage = ArrayList<ListStoryUserPage>()
        for (item in 0..10) {
            val listStory = ListStoryUserPage(
                "$item",
                "Muhammad Rizky",
                "My description is ....",
                "https://www.publicbooks.org/wp-content/uploads/2017/01/book-e1484158615982.jpg",
                "2022-11-09",
                null,
                null,
            )
            listStoryUserPage.add(listStory)
        }
        return listStoryUserPage
    }


    fun setDummyAccountRequestLoginUser(): LoginRequest {
        return LoginRequest("muhammadrizky1023456@gmail.com", "123456")
    }

    fun setDummyAccountResponseLoginUser(): LoginResponse {
        val responseLoginResult= LoginResult("story-HMxvdihSQOZXWh2V", "Muhammad Rizky", "token")
        return LoginResponse(false, "login was successfully", responseLoginResult)
    }

    fun setDummyAccountRequestCreateUser(): RequestRegister {
        return RequestRegister("Muhammad Rizky", "muhammadrizky1023456@gmail.com", "123456")

    }
}
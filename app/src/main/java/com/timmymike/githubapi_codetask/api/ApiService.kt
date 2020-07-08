package com.timmymike.githubapi_codetask.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @Headers("Content-Type: application/json", "Accept: application/json")
    @GET("/users/{name}")
    fun getUserDetail(@Path("name") name: String): Call<ArrayList<UserDetailModel>>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @GET("/search/users")
    fun getSearchData(@Query("q") search:String): Call<UserSearchModel>
}
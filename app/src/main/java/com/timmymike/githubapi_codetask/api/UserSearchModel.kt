package com.timmymike.githubapi_codetask.api

import com.google.gson.annotations.SerializedName


data class UserSearchModel(
    @SerializedName("incomplete_results")
    var incompleteResults: Boolean = false,
    @SerializedName("items")
    var items: List<Item> = listOf(),
    @SerializedName("total_count")
    var totalCount: Int = 0
) {
    data class Item(
        @SerializedName("avatar_url")
        var avatarUrl: String = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("login")
        var login: String = ""
    )
}

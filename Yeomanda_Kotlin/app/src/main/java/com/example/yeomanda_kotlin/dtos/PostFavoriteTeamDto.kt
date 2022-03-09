package com.example.yeomanda_kotlin.dtos

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PostFavoriteTeamDto(
    var email: String,
    @SerializedName("favorite_team_no")
    @Expose
    var favoriteTeamNo: Int

)

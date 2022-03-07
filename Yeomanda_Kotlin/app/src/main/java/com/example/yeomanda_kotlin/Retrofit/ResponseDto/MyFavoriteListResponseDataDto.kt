package com.example.yeomanda_kotlin.Retrofit.ResponseDto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MyFavoriteListResponseDataDto(
        @SerializedName("team_name")
        @Expose
        val teamName : String,
        val member : Int,
        @SerializedName("team_no")
        @Expose
        val teamNum : Int
)

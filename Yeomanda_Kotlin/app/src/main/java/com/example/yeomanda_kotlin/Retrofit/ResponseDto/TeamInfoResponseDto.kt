package com.example.yeomanda_kotlin.Retrofit.ResponseDto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TeamInfoResponseDto(
        val email : List<String>,
        @SerializedName("location_gps")
        @Expose
        val locationGps : String,
        @SerializedName("team_no")
        @Expose
        val teamNum : Int,
        val travelDate : String,
        val isfinished : Int,
        @SerializedName("team_name")
        @Expose
        val teamName : String,
        @SerializedName("name")
        @Expose
        val nameList : List<String>
)

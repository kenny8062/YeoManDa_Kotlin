package com.example.yeomanda_kotlin.dtos

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TeamInfoDto(
    val email: List<String>,
    @SerializedName("location_gps")
    @Expose
    var locationGps: String,

    @SerializedName("team_no")
    @Expose
    val teamNo: Int,

    val travelDate: String,

    val isfinished: Int,

    @SerializedName("team_name")
    @Expose
    val teamName: String,

    @SerializedName("name")
    @Expose
    val nameList: List<String>,
)

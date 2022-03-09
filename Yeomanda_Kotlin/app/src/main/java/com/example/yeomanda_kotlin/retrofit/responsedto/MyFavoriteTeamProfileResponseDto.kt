package com.example.yeomanda_kotlin.retrofit.responsedto

import com.example.yeomanda_kotlin.dtos.MyFavoriteTeamProfileDto

data class MyFavoriteTeamProfileResponseDto(

    var status: Int,
    var success: Boolean,
    var message: String,
    var data: List<MyFavoriteTeamProfileDto>
)

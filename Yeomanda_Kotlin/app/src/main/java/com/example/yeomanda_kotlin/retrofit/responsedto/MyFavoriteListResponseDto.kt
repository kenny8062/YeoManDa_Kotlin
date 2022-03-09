package com.example.yeomanda_kotlin.retrofit.responsedto

data class MyFavoriteListResponseDto(

    val status: Int,
    val success: Boolean,
    val message: String,
    val data: List<MyFavoriteListResponseDataDto>
)

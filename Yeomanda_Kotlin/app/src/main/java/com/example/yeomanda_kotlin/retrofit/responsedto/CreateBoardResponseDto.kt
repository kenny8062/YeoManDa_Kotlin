package com.example.yeomanda_kotlin.retrofit.responsedto

data class CreateBoardResponseDto(
    var status: Int,
    var success: Boolean,
    var message: String,
    var data: String
)

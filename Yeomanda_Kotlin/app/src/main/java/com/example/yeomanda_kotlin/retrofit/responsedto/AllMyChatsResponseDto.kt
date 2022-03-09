package com.example.yeomanda_kotlin.retrofit.responsedto

import com.example.yeomanda_kotlin.dtos.ChatMessages2

data class AllMyChatsResponseDto(

    var status: Int,
    var success: Boolean,
    var message: String,
    var data: List<ChatMessages2>
)

package com.example.yeomanda_kotlin.retrofit.responsedto

import com.example.yeomanda_kotlin.dtos.ChatListDataDto

data class ChatListResponseDto(

    var status: Int,
    var success: Boolean,
    var message: String,
    var data: List<ChatListDataDto>
)

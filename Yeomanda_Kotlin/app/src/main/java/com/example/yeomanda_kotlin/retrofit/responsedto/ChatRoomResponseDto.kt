package com.example.yeomanda_kotlin.retrofit.responsedto

import com.example.yeomanda_kotlin.dtos.ChatRoomDto

data class ChatRoomResponseDto(

    var status: Int,
    var success: Boolean,
    var message: String,
    var data: ChatRoomDto
)

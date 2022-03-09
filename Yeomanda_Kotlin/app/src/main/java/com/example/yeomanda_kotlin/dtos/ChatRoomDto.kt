package com.example.yeomanda_kotlin.dtos

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ChatRoomDto(
        val teams : List<String>,
        @SerializedName("room_id")
        @Expose
        val roomId : String,
        val members : List<String>

)

package com.example.yeomanda_kotlin.Retrofit.ResponseDto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ChatListDataDto(
        @SerializedName("room_id")
        @Expose
        val roomId : String,
        val otherTeamName : String,
        val chatMessages : ChatMessages
)

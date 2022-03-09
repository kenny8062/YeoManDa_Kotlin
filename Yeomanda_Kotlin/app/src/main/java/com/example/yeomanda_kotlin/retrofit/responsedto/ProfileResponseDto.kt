package com.example.yeomanda_kotlin.retrofit.responsedto

import com.example.yeomanda_kotlin.dtos.ProfileDto

data class ProfileResponseDto(
    val status: Int,
    val success: Boolean,
    val message: String,
    val data: ProfileDto,

    )

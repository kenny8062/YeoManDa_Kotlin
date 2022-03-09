package com.example.yeomanda_kotlin.retrofit.responsedto

import com.example.yeomanda_kotlin.dtos.TeamInfoDto

data class LocationResponseDto(
    val status: Int,
    val success: Boolean,
    val message: String,
    val data: List<TeamInfoDto>

)

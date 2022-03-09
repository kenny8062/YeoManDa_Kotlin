package com.example.yeomanda_kotlin.retrofit.responsedto

import com.example.yeomanda_kotlin.dtos.LoginDto

data class LoginResponseDto(
    val status: Int,
    val success: Boolean,
    val message: String,
    val data: LoginDto
)

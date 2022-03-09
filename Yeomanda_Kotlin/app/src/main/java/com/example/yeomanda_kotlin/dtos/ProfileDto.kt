package com.example.yeomanda_kotlin.dtos

data class ProfileDto(
    val email: String,
    val birth: String,

    val sex: String,

    val name: String,

    val files: List<String>
)

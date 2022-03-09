package com.example.yeomanda_kotlin.dtos

data class MyFavoriteTeamProfileDto(
    val email: String,

    val name: String,

    val sex: String,

    val birth: String,

    val files: List<String>
)
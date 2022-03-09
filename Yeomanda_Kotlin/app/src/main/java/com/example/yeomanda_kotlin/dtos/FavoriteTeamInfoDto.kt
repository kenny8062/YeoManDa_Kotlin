package com.example.yeomanda_kotlin.dtos

data class FavoriteTeamInfoDto(
    private var email: List<String>,
    private val name: List<String>,

    private val birth: List<String>,

    private val url: List<String>,

    private val teamName: String
)

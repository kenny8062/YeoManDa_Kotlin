package com.example.yeomanda_kotlin.dtos

import java.io.Serializable

data class JoinDto (
    var email: String,

    var password: String,

    var name: String,

    var birth: String,

    var sex: String
): Serializable

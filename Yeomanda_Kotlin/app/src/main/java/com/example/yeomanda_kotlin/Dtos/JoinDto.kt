package com.example.yeomanda_kotlin.Dtos

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class JoinDto : Serializable {
    var email: String? = null

    var password: String? = null

    var name: String? = null

    var birth: String? = null

    var sex: String? = null
}

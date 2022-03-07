package com.example.yeomanda_kotlin.Retrofit

import com.example.yeomanda_kotlin.Retrofit.ResponseDto.LoginResponseDto
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RetrofitInterface {

    @FormUrlEncoded
    @POST("/user/login")
    fun login(@Field("email") email: String,
              @Field("password") password : String,
              @Field("fcm_token") fcm_token : String) : Call<LoginResponseDto>
}
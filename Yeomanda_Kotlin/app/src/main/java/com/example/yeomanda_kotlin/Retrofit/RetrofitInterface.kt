package com.example.yeomanda_kotlin.Retrofit

import com.example.yeomanda_kotlin.Retrofit.ResponseDto.JoinResponseDto
import com.example.yeomanda_kotlin.Retrofit.ResponseDto.LoginResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitInterface {

    @FormUrlEncoded
    @POST("/user/login")
    fun login(@Field("email") email: String,
              @Field("password") password : String,
              @Field("fcm_token") fcm_token : String) : Call<LoginResponseDto>

    //회원가입
    @Multipart
    @POST("/user/signup")
    fun uploadJoin(
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("name") name: RequestBody,
        @Part("sex") sex: RequestBody,
        @Part("birth") birth: RequestBody,
        @Part totalSelfImage: Array<MultipartBody.Part?>
    ): Call<JoinResponseDto>

}
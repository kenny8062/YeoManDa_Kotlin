package com.example.yeomanda_kotlin.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitService {
    companion object {
        //통신할 서버 url
        private const val baseUrl = "http://192.168.0.20:3000/"

        //Retrofit 객체 초기화
        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val retrofitInterface : RetrofitInterface = retrofit.create(RetrofitInterface::class.java)
    }
}
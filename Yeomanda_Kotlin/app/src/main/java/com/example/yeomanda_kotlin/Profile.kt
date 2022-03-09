package com.example.yeomanda_kotlin

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.yeomanda_kotlin.retrofit.responsedto.ProfileResponseDto
import com.example.yeomanda_kotlin.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Profile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person_info)
        init()
    }

    fun init() {
        val intent = intent
        val selfImage1 = findViewById<ImageView>(R.id.personSubImage1)
        val selfImage2 = findViewById<ImageView>(R.id.personMainImage)
        val selfImage3 = findViewById<ImageView>(R.id.personsubImage2)
        val personEmail = findViewById<TextView>(R.id.personEmail)
        val personSex = findViewById<TextView>(R.id.personSex)
        val personName = findViewById<TextView>(R.id.personName)
        val personBirth = findViewById<TextView>(R.id.personBirth)


        RetrofitService.retrofitInterface.showProfile(
            intent.getStringExtra("token")!!,
            intent.getStringExtra("email")!!
        ).enqueue(object : Callback<ProfileResponseDto>{
            override fun onResponse(
                call: Call<ProfileResponseDto>,
                response: Response<ProfileResponseDto>
            ) {

                personEmail.setText(response.body()?.data?.email)
                personSex.setText(response.body()?.data?.sex)
                personBirth.setText(response.body()?.data?.birth)
                personName.setText(response.body()?.data?.name)
                Glide.with(this@Profile)
                    .load(response.body()?.data?.files!![0])
                    .into(selfImage1)
                Glide.with(this@Profile)
                    .load(response.body()?.data?.files?.get(1))
                    .into(selfImage2)
                Glide.with(this@Profile)
                    .load(response.body()?.data?.files?.get(2))
                    .into(selfImage3)
            }

            override fun onFailure(call: Call<ProfileResponseDto>, t: Throwable) {

                    Log.d("error","ProfileResponseDto is null")
            }

        })
    }
}
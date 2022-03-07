package com.example.yeomanda_kotlin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.yeomanda_kotlin.Retrofit.ResponseDto.LoginResponseDto
import com.example.yeomanda_kotlin.Retrofit.RetrofitService
import com.example.yeomanda_kotlin.SignUp.SignUpActivity1
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    lateinit var fcmToken : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //권한 체크
        val REQUEST_EXTERNAL_STORAGE = 1
        val PERMISSIONS_STORAGE  =arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val writePermission = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val readPermission =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE)
        }

        //FCM 토큰 가져오기
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            Log.d("Tag",it.result)
            fcmToken=it.result }

        init()
    }
    fun init(){
        val emailEdt = findViewById<EditText>(R.id.id_edt)
        val passwordEdt= findViewById<EditText>(R.id.password_edt)
        val joinBtn = findViewById<Button>(R.id.join_btn)
        val loginBtn = findViewById<Button>(R.id.login_btn)
        joinBtn.setOnClickListener {
            var intent =Intent(this, SignUpActivity1::class.java)
            startActivity(intent)
        }

        loginBtn.setOnClickListener {
            RetrofitService.retrofitInterface.login(emailEdt.text.toString(),
                passwordEdt.text.toString(), fcmToken
            ).enqueue(object : Callback<LoginResponseDto>{
                override fun onResponse(
                    call: Call<LoginResponseDto>,
                    response: Response<LoginResponseDto>
                ) {
                    var intent = Intent(applicationContext,MainActivity::class.java)
                    intent.apply {
                        this.putExtra("token",fcmToken)
                        this.putExtra("hasPlanned", response.body()?.hasPlanned)
                        this.putExtra("email",emailEdt.text.toString())
                    }
                    startActivity(intent)
                    finish()
                }
                override fun onFailure(call: Call<LoginResponseDto>, t: Throwable) {
                    Toast.makeText(applicationContext,"로그인 정보가 틀립니다",Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

}
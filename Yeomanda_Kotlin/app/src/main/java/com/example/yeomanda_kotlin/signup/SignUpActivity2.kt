package com.example.yeomanda_kotlin.signup

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.loader.content.CursorLoader
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.yeomanda_kotlin.dtos.JoinDto
import com.example.yeomanda_kotlin.LoginActivity
import com.example.yeomanda_kotlin.R
import com.example.yeomanda_kotlin.retrofit.responsedto.WithoutDataResponseDto
import com.example.yeomanda_kotlin.retrofit.RetrofitService
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class SignUpActivity2 : AppCompatActivity() {

    lateinit var joinDto : JoinDto
    lateinit var nextBtn: Button
    var joinMyMainImage: ImageView? = null; var joinMySubImage1 : ImageView?= null ; var joinMySubImage2 : ImageView?= null
    var selfimage = arrayOfNulls<MultipartBody.Part>(3)
    var uri = arrayOfNulls<Uri>(3)
    var isComplete = booleanArrayOf(false, false, false)
    val MY_PERMISSIONS_REQUEST_READ_EXT_STORAGE = 1
    private val GET_IMAGE_FOR_PICTURE1 = 300
    private val GET_IMAGE_FOR_PICTURE2 = 301
    private val GET_IMAGE_FOR_PICTURE3 = 302
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up2)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 1)

        joinDto = intent.getSerializableExtra("joinDto") as JoinDto
        init()
    }
    fun init(){
        nextBtn = findViewById(R.id.nextBtn3)
        joinMyMainImage = findViewById(R.id.joinMyMainImage)
        joinMySubImage1 = findViewById(R.id.joinMySubImage1)
        joinMySubImage2 = findViewById(R.id.joinMySubImage2)

        nextBtn.setOnClickListener {
            for (i in 0..2) {
                selfimage[i] = RetrofitService.prepareFilePart(
                    "files", uri[i]!!, applicationContext
                )
            }
            RetrofitService.retrofitInterface.uploadJoin(RetrofitService.createPartFromString(joinDto.email!!),RetrofitService.createPartFromString(joinDto.password!!),RetrofitService.createPartFromString(joinDto.name!!),RetrofitService.createPartFromString(joinDto.sex!!),RetrofitService.createPartFromString(joinDto.birth!!),selfimage)
                .enqueue(object :
                    Callback<WithoutDataResponseDto>{
                    override fun onResponse(
                        call: Call<WithoutDataResponseDto>,
                        response: Response<WithoutDataResponseDto>
                    ) {
                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intent)
                        finish()

                    }

                    override fun onFailure(call: Call<WithoutDataResponseDto>, t: Throwable) {
                       Log.d("error",t.message.toString())
                    }
                })

        }
        joinMyMainImage!!.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            startActivityForResult(intent, GET_IMAGE_FOR_PICTURE1)
        }
        joinMySubImage1!!.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            startActivityForResult(intent, GET_IMAGE_FOR_PICTURE2)
        }
        joinMySubImage2!!.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            startActivityForResult(intent, GET_IMAGE_FOR_PICTURE3)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val multiOption =MultiTransformation(CenterCrop(), RoundedCorners(8))
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null && data.data != null) {
            when (requestCode) {
                GET_IMAGE_FOR_PICTURE1 -> {
                    uri[0] = data.data!!
                    Glide.with(applicationContext).asBitmap().load(uri[0])
                        .apply(RequestOptions.bitmapTransform(multiOption)).into(
                            joinMyMainImage!!
                        )
                    isComplete[0] = true
                    if (isComplete[0] && isComplete[1] && isComplete[2]) onNextBtn()
                }
                GET_IMAGE_FOR_PICTURE2 -> {
                    uri[1] = data.data!!
                    Glide.with(applicationContext).asBitmap().load(uri[1])
                        .apply(RequestOptions.bitmapTransform(multiOption)).into(
                            joinMySubImage1!!
                        )
                    isComplete[1] = true
                    if (isComplete[0] && isComplete[1] && isComplete[2]) onNextBtn()
                }
                GET_IMAGE_FOR_PICTURE3 -> {
                    uri[2] = data.data!!
                    Glide.with(applicationContext).asBitmap().load(uri[2])
                        .apply(RequestOptions.bitmapTransform(multiOption)).into(
                            joinMySubImage2!!
                        )
                    isComplete[2] = true
                    if (isComplete[0] && isComplete[1] && isComplete[2]) onNextBtn()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_EXT_STORAGE -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }

    // 다음 버튼 활성화
    private fun onNextBtn() {
        nextBtn.setBackgroundResource(R.drawable.ic_pale_sky_blue_rounded_rectangle)
        nextBtn.setTextColor(resources.getColor(R.color.black))
        nextBtn.isEnabled = true
    }
}
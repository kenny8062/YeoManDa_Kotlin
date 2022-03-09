package com.example.yeomanda_kotlin

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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.yeomanda_kotlin.retrofit.RetrofitService
import com.example.yeomanda_kotlin.retrofit.responsedto.ProfileResponseDto
import com.example.yeomanda_kotlin.retrofit.responsedto.WithoutDataResponseDto
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyProfile : AppCompatActivity() {
    val context : Context = this

    val MY_PERMISSIONS_REQUEST_READ_EXT_STORAGE = 1
    private val GET_IMAGE_FOR_PICTURE1 = 300
    private val GET_IMAGE_FOR_PICTURE2 = 301
    private val GET_IMAGE_FOR_PICTURE3 = 302

    var mySubImage1 : ImageView?=null; var myMainImage : ImageView?=null; var mySubImage2 : ImageView?=null
    var uri = arrayOfNulls<Uri>(3)
    var isUri = booleanArrayOf(false, false, false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 1)
        init()
    }
    fun init(){
        val myToken = intent.getStringExtra("token")!!
        val myEmail = findViewById<TextView>(R.id.myEmail)
        val mySex = findViewById<TextView>(R.id.mySex)
        val myName = findViewById<TextView>(R.id.myName)
        val myBirth = findViewById<TextView>(R.id.myBirth)
        val editMyProfileBtn = findViewById<Button>(R.id.editMyProfileBtn)
        mySubImage1 = findViewById<ImageView>(R.id.myImage1)
        myMainImage = findViewById<ImageView>(R.id.myMainImage)
        mySubImage2 = findViewById<ImageView>(R.id.myImage2)

        RetrofitService.retrofitInterface.getMyProfile(myToken).enqueue(object : Callback<ProfileResponseDto>{
            override fun onResponse(
                call: Call<ProfileResponseDto>,
                response: Response<ProfileResponseDto>
            ) {
                myEmail.text=response.body()?.data?.email!!
                mySex.text=response.body()?.data?.sex
                myBirth.text=response.body()?.data?.birth
                myName.text=response.body()?.data?.name

                Glide.with(this@MyProfile)
                    .load(response.body()?.data?.files!![0])
                    .into(myMainImage!!)

                Glide.with(this@MyProfile)
                    .load(response.body()?.data?.files!![0])
                    .into(mySubImage1!!)

                Glide.with(this@MyProfile)
                    .load(response.body()?.data?.files!![0])
                    .into(mySubImage2!!)


                editMyProfileBtn.setOnClickListener {
                    var selfImage = ArrayList<MultipartBody.Part>()
                    var changeUri = ArrayList<MultipartBody.Part>()
                    for (i in 0..2) {
                        if (isUri[i]) {
                            selfImage.add(
                                RetrofitService.prepareFilePart(
                                    "files",
                                    uri[i]!!,
                                    context
                                )
                            )
                            //changeUri.add(retrofitClient.createPartFromString(profileResponseDto.getData().getFiles().get(i)));
                            changeUri.add(
                                MultipartBody.Part.createFormData(
                                    "updatedURI",
                                    response.body()?.data?.files!![i]
                                )
                            )
                        }
                    }
                    RetrofitService.retrofitInterface.updateMyProfile(
                        myToken,
                        RetrofitService.createPartFromString(response.body()?.data?.email!!),
                        selfImage,
                        changeUri
                    ).enqueue(object : Callback<WithoutDataResponseDto>{
                        override fun onResponse(
                            call: Call<WithoutDataResponseDto>,
                            response: Response<WithoutDataResponseDto>
                        ) {

                            finish()
                        }

                        override fun onFailure(call: Call<WithoutDataResponseDto>, t: Throwable) {
                            Log.d("error",t.message.toString())
                        }

                    })
                }

                myMainImage!!.setOnClickListener {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "image/*"
                    )
                    startActivityForResult(intent, GET_IMAGE_FOR_PICTURE1)
                }
                mySubImage1!!.setOnClickListener {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "image/*"
                    )
                    startActivityForResult(intent, GET_IMAGE_FOR_PICTURE2)
                }
                mySubImage2!!.setOnClickListener {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "image/*"
                    )
                    startActivityForResult(intent, GET_IMAGE_FOR_PICTURE3)
                }
            }

            override fun onFailure(call: Call<ProfileResponseDto>, t: Throwable) {
                Log.d("error",t.message.toString())
            }

        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var selectedImageUri: Uri?
        val multiOption=
            MultiTransformation(CenterCrop(), RoundedCorners(8))
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null && data.data != null) {
            when (requestCode) {
                GET_IMAGE_FOR_PICTURE1 -> {
                    selectedImageUri = data.data
                    uri[0] = selectedImageUri
                    isUri[0] = true
                    myMainImage?.let {
                        Glide.with(applicationContext).asBitmap().load(selectedImageUri)
                            .apply(RequestOptions.bitmapTransform(multiOption)).into(it)
                    }
                }
                GET_IMAGE_FOR_PICTURE2 -> {
                    selectedImageUri = data.data
                    uri[1] = selectedImageUri
                    isUri[1] = true
                    mySubImage1?.let {
                        Glide.with(applicationContext).asBitmap().load(selectedImageUri)
                            .apply(RequestOptions.bitmapTransform(multiOption)).into(it)
                    }
                }
                GET_IMAGE_FOR_PICTURE3 -> {
                    selectedImageUri = data.data
                    uri[2] = selectedImageUri
                    isUri[2] = true
                    mySubImage2?.let {
                        Glide.with(applicationContext).asBitmap().load(selectedImageUri)
                            .apply(RequestOptions.bitmapTransform(multiOption)).into(it)
                    }
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
}
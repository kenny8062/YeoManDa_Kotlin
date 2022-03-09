package com.example.yeomanda_kotlin.retrofit

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.loader.content.CursorLoader
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

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

        fun createPartFromString(descriptionString: String): RequestBody {
            return RequestBody.create(
                MediaType.parse("text/plain"), descriptionString
            )
        }
        private fun getRealPathFromURI(contentUri: Uri, context: Context): String {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            val loader = CursorLoader(context, contentUri, proj, null, null, null)
            val cursor = loader.loadInBackground()
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            val result = cursor.getString(column_index)
            cursor.close()
            return result
        }
        fun prepareFilePart(partName: String, fileUri: Uri, context: Context): MultipartBody.Part {
            val file= File(getRealPathFromURI(fileUri, context))
            val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
            return MultipartBody.Part.createFormData(partName, file.name, requestFile)
        }
    }

}
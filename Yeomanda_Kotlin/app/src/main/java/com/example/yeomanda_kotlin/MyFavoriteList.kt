package com.example.yeomanda_kotlin

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.yeomanda_kotlin.listviewpkg.FavoriteTeamListViewAdapter
import com.example.yeomanda_kotlin.retrofit.responsedto.MyFavoriteListResponseDto
import com.example.yeomanda_kotlin.retrofit.responsedto.WithoutDataResponseDto
import com.example.yeomanda_kotlin.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyFavoriteList : AppCompatActivity() {
    lateinit var myToken : String
    lateinit var myFavoriteTeamListView: ListView
    private val context : Context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_favorite_list)

        myToken=intent.getStringExtra("token")!!
        init()
    }

    fun init(){

        RetrofitService.retrofitInterface.showMyFavoriteTeam(myToken).enqueue(object : Callback<MyFavoriteListResponseDto>{
            @SuppressLint("SetTextI18n", "ResourceAsColor")
            override fun onResponse(
                call: Call<MyFavoriteListResponseDto>,
                response: Response<MyFavoriteListResponseDto>
            ) {
                if (response.body()?.data != null) {
                    myFavoriteTeamListView = findViewById<ListView>(R.id.myFavoriteTeamListView)
                    val adapter = FavoriteTeamListViewAdapter()
                    myFavoriteTeamListView.adapter = adapter
                    for (i in 0 until response.body()?.data?.size!!) {
                        adapter.addItem(
                            response.body()?.data!![i].teamName,
                            response.body()?.data!![i].member
                        )
                    }
                }
                myFavoriteTeamListView.setOnItemClickListener { parent, view, position, id ->
                    val intent = Intent(applicationContext, MyFavoriteTeamProfile::class.java)
                    intent.putExtra(
                        "teamName",
                        response.body()?.data!![position].teamName
                    )
                    intent.putExtra("token", myToken)
                    startActivity(intent)
                }

                //LongClickListener는 true반환시 longclicklistener만 작동, false반환시 일반 clicklistener도 동작
                myFavoriteTeamListView.setOnItemLongClickListener { parent, view, position, id ->
                    val builder = AlertDialog.Builder(context)
                    val textView = TextView(applicationContext)
                    textView.text ="\"" + response.body()?.data!![position].teamName + "\"팀을\n즐겨찾기에서 삭제 하시겠습니까?"
                    textView.gravity = Gravity.CENTER
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                    textView.setTextColor(R.color.back)

                    builder.setView(textView)
                    //builder.setMessage(myFavoriteListResponseDto.getData().get(position).getTeamName()+"팀의\n즐겨찾기를 취소 하시겠습니까?");
                    builder.setPositiveButton("확인") { dialog, id -> // User clicked OK button
                        RetrofitService.retrofitInterface.deleteFavoriteTeam(myToken,response.body()?.data!![position].teamNum).enqueue(
                            object : Callback<WithoutDataResponseDto>{
                                override fun onResponse(
                                    call: Call<WithoutDataResponseDto>,
                                    response: Response<WithoutDataResponseDto>
                                ) {
                                    if(response.body()?.success == true) init()
                                }

                                override fun onFailure(
                                    call: Call<WithoutDataResponseDto>,
                                    t: Throwable
                                ) {
                                    Log.d("error", " withoutDataResponseDto is null")
                                }

                            }
                        )

                    }
                    builder.setNegativeButton(
                        "취소"
                    ) { dialog, id ->
                        // User cancelled the dialog
                    }
                    val alertDialog = builder.create()
                    alertDialog.show()
                    return@setOnItemLongClickListener true
                }
            }
            override fun onFailure(call: Call<MyFavoriteListResponseDto>, t: Throwable) {
                Log.d("error", t.message.toString())
            }

        })
    }
}
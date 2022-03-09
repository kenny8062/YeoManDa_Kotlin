package com.example.yeomanda_kotlin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.yeomanda_kotlin.listviewpkg.TeamInfoListViewAdapter
import com.example.yeomanda_kotlin.retrofit.responsedto.MyFavoriteTeamProfileResponseDto
import com.example.yeomanda_kotlin.retrofit.responsedto.ProfileResponseDto
import com.example.yeomanda_kotlin.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyFavoriteTeamProfile : AppCompatActivity() {
    var profileDialogView:View? = null
    lateinit var listView :ListView
    var context: Context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_favorite_team_profile)
        init()
    }
    fun init(){
        val myToken : String = intent.getStringExtra("token")!!
        val teamName : String = intent.getStringExtra("teamName")!!
        RetrofitService.retrofitInterface.showMyFavriteTeamProfile(myToken,teamName).enqueue(
            object : Callback<MyFavoriteTeamProfileResponseDto>{
                override fun onResponse(
                    call: Call<MyFavoriteTeamProfileResponseDto>,
                    response: Response<MyFavoriteTeamProfileResponseDto>
                ) {
                    listView = findViewById<ListView>(R.id.myFavoriteTeamProfileListView)
                    val adapter =TeamInfoListViewAdapter()
                    listView.adapter = adapter
                    for (i in response.body()?.data!!) {
                        adapter.addItem(
                            i.files[0],
                            i.name,
                            i.sex,
                            i.birth
                        )
                    }

                    listView.setOnItemClickListener { parent, view, position, id ->
                        profileDialogView =
                            layoutInflater.inflate(R.layout.activity_person_info, null)
                        val builder = AlertDialog.Builder(context)
                        builder.setView(profileDialogView)

                        val alertDialog = builder.create()
                        alertDialog.show()
                        val personMainImage =
                            alertDialog.findViewById<ImageView>(R.id.personMainImage)
                        val personSubImage1 =
                            alertDialog.findViewById<ImageView>(R.id.personSubImage1)
                        val personSubImage2 =
                            alertDialog.findViewById<ImageView>(R.id.personsubImage2)
                        val personEmail = alertDialog.findViewById<TextView>(R.id.personEmail)
                        val personSex = alertDialog.findViewById<TextView>(R.id.personSex)
                        val personName = alertDialog.findViewById<TextView>(R.id.personName)
                        val personBirth = alertDialog.findViewById<TextView>(R.id.personBirth)
                        RetrofitService.retrofitInterface.showProfile(myToken, response.body()?.data!![position].email).enqueue(
                            object : Callback<ProfileResponseDto>{
                                override fun onResponse(
                                    call: Call<ProfileResponseDto>,
                                    response: Response<ProfileResponseDto>
                                ) {

                                    personEmail?.text = response.body()?.data?.email
                                    personSex?.text = response.body()?.data?.sex
                                    personBirth?.text = response.body()?.data?.birth
                                    personName?.text = response.body()?.data?.name
                                    if (personMainImage != null) {
                                        Glide.with(context)
                                            .load(response.body()?.data?.files?.get(0))
                                            .into(personMainImage)
                                    }
                                    if (personSubImage1 != null) {
                                        Glide.with(context)
                                            .load(response.body()?.data?.files?.get(1))
                                            .into(personSubImage1)
                                    }
                                    if (personSubImage2 != null) {
                                        Glide.with(context)
                                            .load(response.body()?.data?.files?.get(2))
                                            .into(personSubImage2)
                                    }

                                    //이미지 크게 보기1
                                    personSubImage1?.setOnClickListener(View.OnClickListener {
                                        val intent =
                                            Intent(applicationContext, SelectImageActivity::class.java)
                                        intent.putExtra(
                                            "uri",
                                            response.body()?.data?.files?.get(1)
                                        )
                                        startActivity(intent)
                                    })
                                    //이미지 크게 보기2
                                    personMainImage?.setOnClickListener(View.OnClickListener {
                                        val intent =
                                            Intent(applicationContext, SelectImageActivity::class.java)
                                        intent.putExtra(
                                            "uri",
                                            response.body()?.data?.files?.get(0)
                                        )
                                        startActivity(intent)
                                    })
                                    //이미지 크게 보기3
                                    personSubImage2?.setOnClickListener(View.OnClickListener {
                                        val intent =
                                            Intent(applicationContext, SelectImageActivity::class.java)
                                        intent.putExtra(
                                            "uri",
                                            response.body()?.data?.files?.get(2)
                                        )
                                        startActivity(intent)
                                    })
                                }

                                override fun onFailure(
                                    call: Call<ProfileResponseDto>,
                                    t: Throwable
                                ) {
                                    Log.d("error","ProfileResponseDto is null")
                                }

                            }
                        )
                    }

                }

                override fun onFailure(call: Call<MyFavoriteTeamProfileResponseDto>, t: Throwable) {
                    Log.d("error", "myFavoriteTeamProfileResponseDto is null")
                }

            })
    }
}
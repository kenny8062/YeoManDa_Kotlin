package com.example.yeomanda_kotlin.chatpkg

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yeomanda_kotlin.R
import com.example.yeomanda_kotlin.listviewpkg.ChatListViewAdapter
import com.example.yeomanda_kotlin.retrofit.RetrofitService
import com.example.yeomanda_kotlin.retrofit.responsedto.ChatListResponseDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatListActivity : AppCompatActivity() {
    lateinit var myToken : String
    lateinit var myEmail : String
    lateinit var listview: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)
        myToken=intent.getStringExtra("token")!!
        myEmail=intent.getStringExtra("myEmail")!!
        init()
    }
    fun init(){

        RetrofitService.retrofitInterface.getChatList(myToken).enqueue(object : Callback<ChatListResponseDto>{
            override fun onResponse(
                call: Call<ChatListResponseDto>,
                response: Response<ChatListResponseDto>
            ) {
                val adapter = ChatListViewAdapter()
                listview = findViewById(R.id.chatListView)
                listview.adapter = adapter
                if (response.body()?.success == true) {
                    for (i in 0 until response.body()?.data?.size!!) {
                        adapter.addItem(
                            response.body()?.data!![i].otherTeamName,
                            response.body()?.data!![i].chatMessages.content,
                            response.body()?.data!![i].chatMessages.createdAt.split(" ")[4]
                        )
                    }
                    listview.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            val intent = Intent(applicationContext, ChatActivity::class.java)
                            intent.putExtra("roomId", response.body()?.data!![position].roomId)
                            intent.putExtra("token", myToken)
                            intent.putExtra("myEmail", myEmail)
                            startActivity(intent)
                        }
                } else {
                    Toast.makeText(applicationContext, "채팅방이 없습니다.", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<ChatListResponseDto>, t: Throwable) {
                Log.e("error", t.message.toString())
            }

        })
    }
}
package com.example.yeomanda_kotlin.chatpkg

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.yeomanda_kotlin.R
import com.example.yeomanda_kotlin.listviewpkg.ChatMessageAdapter
import com.example.yeomanda_kotlin.retrofit.RetrofitService
import com.example.yeomanda_kotlin.retrofit.responsedto.AllMyChatsResponseDto
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URISyntaxException

class ChatActivity : AppCompatActivity() {
    lateinit var mSocket : Socket
    lateinit var adapter: ChatMessageAdapter
    lateinit var listView : ListView
    lateinit var myEmail : String
    var roomInfo: JSONObject? = null
    var isMyChat: Boolean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        init()
    }
    fun init(){
        var myToken : String = intent.getStringExtra("token")!!
        val roomId : String= intent.getStringExtra("roomId")!!
        myEmail= intent.getStringExtra("myEmail")!!
        val sendBtn = findViewById<Button>(R.id.sendChatBtn)
        val chatEdt = findViewById<EditText>(R.id.chatEdt)
        listView = findViewById(R.id.chatMsgListView)

        RetrofitService.retrofitInterface.getAllMyChats(myToken,roomId).enqueue(object : Callback<AllMyChatsResponseDto>{
            override fun onResponse(
                call: Call<AllMyChatsResponseDto>,
                response: Response<AllMyChatsResponseDto>
            ) {
                adapter = ChatMessageAdapter()
                listView.adapter = adapter
                listView.transcriptMode = ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL
                for (i in 0 until response.body()?.data?.size!!) {
                    isMyChat = response.body()?.data!![i].senderEmail == myEmail
                    adapter.addItem(
                        response.body()?.data!![i].senderName,
                        response.body()?.data!![i].content,
                        response.body()?.data!![i].createdAt,
                        isMyChat!!
                    )
                }


                adapter.notifyDataSetChanged()
                listView.setSelection(adapter.count - 1)
                Log.d("teamNum", roomId)
                roomInfo = JSONObject()

                try {
                    roomInfo!!.put("token", myToken)
                    roomInfo!!.put("room_id", roomId)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }


                try {
                    mSocket = IO.socket("http://192.168.0.20:3000/")
                    //mSocket=IO.socket("http://172.30.1.7:3000/");
                    //????????? connect ????????? ??????
                    mSocket.connect()
                    Log.d("connect", "ok")
                } catch (e: URISyntaxException) {
                    Log.e("ChatSocketError", e.reason)
                    e.printStackTrace()
                }
                mSocket.on(Socket.EVENT_CONNECT, onConnect)
                mSocket.on("message", onMessageRecieved)

                sendBtn.setOnClickListener {
                    var chat: JSONObject? = JSONObject()
                    try {
                        chat = convertMessageToJsonObject(myToken, roomId, chatEdt.text.toString())
                        mSocket.emit("message", chat)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<AllMyChatsResponseDto>, t: Throwable) {

                Log.e("error", t.message.toString())
            }

        })
    }

    var onConnect = Emitter.Listener {
        mSocket.emit("chatRoom", roomInfo)
        //mSocket.emit("connection","????????????");
    }


    //???????????? ???????????? ??????????????? ?????????
    var onMessageRecieved = Emitter.Listener { args ->
        val receiveData = args[0] as JSONObject
        Log.d("MessageReceived", receiveData.toString())
        try {
            isMyChat = receiveData.getString("senderEmail") == myEmail
            adapter.addItem(
                receiveData.getString("senderName"), receiveData.getString("message"),
                receiveData.getString("time").split(" ").toTypedArray()[4], isMyChat!!
            )
            val msg: Message = handler.obtainMessage()
            handler.sendMessage(msg)
            Log.d("addMessage", receiveData.getString("time"))
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        // mSocket.emit("chat1", receivedata.toString());
        //mSocket.emit("connection","????????????");
    }

    //??????????????? JSONObject??? ??????
    @Throws(JSONException::class)
    fun convertMessageToJsonObject(token: String?, roomId: String?, content: String?): JSONObject? {
        val sendObject = JSONObject()
        sendObject.put("token", token)
        sendObject.put("room_id", roomId)
        sendObject.put("content", content)
        return sendObject
    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket.close()
        Log.d("end", "chatroom")
    }

    //?????????????????? ?????? ????????????????????? UI ????????? ?????????????????? Handler ??????
    @SuppressLint("HandlerLeak")
    val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            // ?????? ????????? ?????? (UI?????? ?????? ???)
            adapter.notifyDataSetChanged()
            listView.setSelection(adapter.count - 1)
        }
    }
}
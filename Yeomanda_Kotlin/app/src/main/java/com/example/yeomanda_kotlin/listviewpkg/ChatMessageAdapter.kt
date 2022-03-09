package com.example.yeomanda_kotlin.listviewpkg

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.example.yeomanda_kotlin.R
import com.example.yeomanda_kotlin.dtos.ChatMessageItem
import kotlin.coroutines.coroutineContext


class ChatMessageAdapter  // ListViewAdapter의 생성자
    : BaseAdapter() {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private val listViewItemList: ArrayList<ChatMessageItem> = ArrayList<ChatMessageItem>()

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    override fun getCount(): Int {
        return listViewItemList.size
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View? {
        var convertView = view
        val pos = position
        val context = parent?.context

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            val inflater =
                context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.chatting_message_list_item, parent, false)
        }
        val linearLayout = convertView?.findViewById<LinearLayout>(R.id.chatMessageLayout)
        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        val nameTextView = convertView?.findViewById<TextView>(R.id.userName)
        val contentTextView = convertView?.findViewById<TextView>(R.id.chatmessage)
        val timeTextView = convertView?.findViewById<TextView>(R.id.msgTime)
        // LoginResponseDataDto Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        val listViewItem: ChatMessageItem = listViewItemList[position]
        if (listViewItem.isMyChat) {
            linearLayout?.gravity = Gravity.RIGHT
            contentTextView?.setBackgroundResource(R.drawable.ic_pale_sky_blue_rounded_rectangle)
            nameTextView?.text = ""
        } else {
            linearLayout?.gravity = Gravity.LEFT
            contentTextView?.setBackgroundResource(R.drawable.ic_light_yellow_rounded_rectangle)
            nameTextView?.text = listViewItem.userName
        }
        // 아이템 내 각 위젯에 데이터 반영
        contentTextView?.text = listViewItem.msg
        timeTextView?.text = listViewItem.msgTime
        return convertView
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    override fun getItem(position: Int): Any {
        return listViewItemList[position]
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    fun addItem(name: String, content: String, time: String, isMyChat: Boolean) {
        val item = ChatMessageItem(name,content,time,isMyChat)
        listViewItemList.add(item)
    }
}
package com.example.yeomanda_kotlin.listviewpkg

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.yeomanda_kotlin.dtos.TeamInfoListViewItemDto
import com.example.yeomanda_kotlin.R

class TeamInfoListViewAdapter()  // ListViewAdapter의 생성자
    : BaseAdapter() {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private val listViewItemList: ArrayList<TeamInfoListViewItemDto> =
        ArrayList()

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    override fun getCount(): Int {
        return listViewItemList.size
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View? {
        var convertView = view
        val pos = position
        val context = parent!!.context

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            val inflater =
                context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.favorite_team_item, parent, false)
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        val imageView = convertView?.findViewById<ImageView>(R.id.favoriteTeamPersonImage)
        val nameTextView = convertView?.findViewById<TextView>(R.id.favoriteTeamPersonName)
        val sexTextView = convertView?.findViewById<TextView>(R.id.favoriteTeamPersonSex)
        val birthTextView = convertView?.findViewById<TextView>(R.id.favoriteTeamPersonBirth)

        // LoginResponseDataDto Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        val listViewItem: TeamInfoListViewItemDto = listViewItemList[position]

        // 아이템 내 각 위젯에 데이터 반영
        Glide.with(context)
            .load(listViewItem.favoriteTeamPersonImage)
            .into(imageView!!)
        nameTextView?.text = listViewItem.favoriteTeamPersonName
        sexTextView?.text = listViewItem.favoriteTeamPersonSex
        birthTextView?.text = listViewItem.favoriteTeamPersonBirth
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
    fun addItem(image: String, name: String, sex: String, birth: String) {
        val item = TeamInfoListViewItemDto(image,name,sex,birth)
        listViewItemList.add(item)
    }
}

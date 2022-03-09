package com.example.yeomanda_kotlin

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class SelectImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_image)
        var selectImageView = findViewById<ImageView>(R.id.selectPersonImage)
        var uri = intent.getStringExtra("uri")
        Glide.with(this).load(uri).into(selectImageView)
    }
}
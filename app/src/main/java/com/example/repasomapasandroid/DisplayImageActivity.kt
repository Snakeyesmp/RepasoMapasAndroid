package com.example.repasomapasandroid

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class DisplayImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_image)

        val imageView: ImageView = findViewById(R.id.imageView)
        val byteArray = intent.getByteArrayExtra("image")

        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray?.size ?: 0)
        imageView.setImageBitmap(bitmap)
    }
}
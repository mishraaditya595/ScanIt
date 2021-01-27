package com.vob.scanit.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.vob.scanit.R

class ShowQRData : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_q_r_data)

        val data = intent.getStringExtra("data")
        val tv = findViewById<TextView>(R.id.tv)
        if (data != null) {

            tv.text = data
        } else
            tv.text = "Nothing found"

    }
}
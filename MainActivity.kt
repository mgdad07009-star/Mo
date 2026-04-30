package com.android.parent.monitor

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        tvStatus.text = "جاري مراقبة طفلك الآن..."

        // إعداد عرض الرسائل الملتقطة (مثال)
        val rvMessages = findViewById<RecyclerView>(R.id.rvMessages)
        rvMessages.layoutManager = LinearLayoutManager(this)
        // هنا يتم ربط البيانات القادمة من Firebase أو السيرفر
    }
}

package com.example.helloworld.share

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.helloworld.R


class ReceiveActivity : AppCompatActivity() {

    companion object {
        const val MY_ACTION_SEND = "android.intent.action.SEND2"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receive)

        val receivedIntent = intent
        val action = receivedIntent.action
        val type = receivedIntent.type

        val tvData = findViewById<TextView>(R.id.tvData)
        if (Intent.ACTION_SEND == action && type != null) {
            if (type.startsWith("image/")) {
                // 处理接收到的图片数据
                val imageUri: Uri? = receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM)
                tvData.text = "接收到视频数据：${imageUri?.toString()}"
            } else if (type.startsWith("video/")) {
                // 处理接收到的视频数据
                val videoUri: Uri? = receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM)
                tvData.text = "接收到图片数据：${videoUri?.toString()}"

            } else if (type.startsWith("text/")) {
                val textData = intent.getStringExtra(Intent.EXTRA_TEXT)
                // 在这里使用textData进行文本数据的处理
                tvData.text = "接收到文本数据：${textData}"
            } else {
                tvData.text = "不支持的数据格式"
            }
        }

    }
}
package com.example.helloworld.ipc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Messenger
import com.example.helloworld.R

class ServiceMessengerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_send)

//        intent.getParcelableExtra<Messenger>()
    }
}
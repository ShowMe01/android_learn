package com.example.helloworld.ipc

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.Messenger
import com.example.helloworld.base.BaseViewBindingActivity
import com.example.helloworld.databinding.ActivityTestMessengerBinding

class ClientMessengerActivity : BaseViewBindingActivity<ActivityTestMessengerBinding>() {

    var handler: Handler? = null
    var remoteMessenger: Messenger? = null

    override fun init() {

        val looper = Looper.myLooper()
        if (looper != null) {
            handler = object : Handler(looper) {
                override fun handleMessage(msg: Message) {
                    when (msg.what) {

                    }
                }
            }
            startActivity(Intent(this, ServiceMessengerActivity::class.java).apply {
                putExtra("ss", Messenger(handler))
            })

        }


    }

    override fun viewBinding(): ActivityTestMessengerBinding {
        return ActivityTestMessengerBinding.inflate(layoutInflater)
    }
}
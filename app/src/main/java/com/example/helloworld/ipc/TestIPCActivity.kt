package com.example.helloworld.ipc

import android.content.Intent
import android.os.Bundle
import com.example.helloworld.base.BaseViewBindingActivity
import com.example.helloworld.databinding.ActivityTestIpcactivityBinding

class TestIPCActivity : BaseViewBindingActivity<ActivityTestIpcactivityBinding>() {

    override fun viewBinding(): ActivityTestIpcactivityBinding {
        return ActivityTestIpcactivityBinding.inflate(layoutInflater)
    }

    override fun init() {
        viewBinding.btnBundle.setOnClickListener {
            val data = Bundle()
            data.putString("key1", "stringValue")
            startActivity(Intent(this, ReceiveBundleActivity::class.java).apply {
                putExtra("keyExtra", data)
            })
        }

        viewBinding.btnMessenger.setOnClickListener {
            startActivity(Intent(this, ClientMessengerActivity::class.java))
        }
    }
}
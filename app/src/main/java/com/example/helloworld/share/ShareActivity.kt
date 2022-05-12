package com.example.helloworld.share

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.helloworld.databinding.ActivityShareBinding

class ShareActivity : AppCompatActivity() {

    companion object{
        private const val TAG = "ShareActivity"
    }

    private lateinit var binding:ActivityShareBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sendText.setOnClickListener {
            ShareUtils.shareText(this, "HelloWorld!", "Share")
        }

        binding.sendBinaryContent.setOnClickListener {
//            ShareUtils.shareBinaryContent(this, "image/jpeg")
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
    }
}
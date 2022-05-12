package com.example.helloworld.share

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.helloworld.R
import com.example.helloworld.databinding.ActivityShareBinding
import java.io.*

class ShareActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ShareActivity"
    }

    private lateinit var binding: ActivityShareBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sendText.setOnClickListener {
            ShareUtils.shareText(this, "HelloWorld!", "Share")
        }

        binding.sendBitmap.setOnClickListener {
            ShareUtils.shareImageToQQ(
                this,
                BitmapFactory.decodeResource(resources, R.drawable.number_1)
            )
        }

        binding.sendFile.setOnClickListener {
            val file = File(filesDir, "videos/NewTextFile.txt")
            file.createNewFile()
            val os = FileOutputStream(file)
            val writer = OutputStreamWriter(os, "UTF-8")
            writer.write("哈哈哈")
            writer.close()
            os.close()
            val uri = FileProvider.getUriForFile(
                this,
                "$packageName.fileprovider",
                file
            )

            ShareUtils.sendFileToApp(
                this,
                uri,
                "text/plain",
                ShareUtils.PACKAGE_MOBILE_QQ,
                ShareUtils.ACTIVITY_MOBILE_QQ,
                "分享哈哈"
            )
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
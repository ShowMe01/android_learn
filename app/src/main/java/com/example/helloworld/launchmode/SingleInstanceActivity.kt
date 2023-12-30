package com.example.helloworld.launchmode

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.helloworld.R

class SingleInstanceActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_singleinstance)
        findViewById<View>(R.id.btnStart).setOnClickListener {
            startActivity(Intent(this, SingleInstanceActivity::class.java))
        }
    }
}
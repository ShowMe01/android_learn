package com.example.helloworld.launchmode

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.helloworld.R

class StandardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_standard)
        findViewById<View>(R.id.btnStart).setOnClickListener {
            startActivity(Intent(this, StandardActivity::class.java))
        }
    }
}
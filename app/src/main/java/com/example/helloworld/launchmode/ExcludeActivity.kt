package com.example.helloworld.launchmode

import android.os.Bundle
import android.widget.TextView
import com.example.helloworld.R

class ExcludeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exclude)
        findViewById<TextView>(R.id.tvName).setText("this is $this")

    }
}
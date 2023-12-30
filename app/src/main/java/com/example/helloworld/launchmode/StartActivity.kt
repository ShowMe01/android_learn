package com.example.helloworld.launchmode

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.helloworld.R

class StartActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        findViewById<View>(R.id.testStandard).setOnClickListener {
            startActivity(Intent(this, StandardActivity::class.java))
        }

        findViewById<View>(R.id.testSingleTop).setOnClickListener {
            startActivity(Intent(this, SingleTopActivity::class.java))
        }

        findViewById<View>(R.id.testSingleTask).setOnClickListener {
            startActivity(Intent(this, SingleTaskActivity::class.java))
        }

        findViewById<View>(R.id.testSingleInstance).setOnClickListener {
            startActivity(Intent(this, SingleInstanceActivity::class.java))
        }
        findViewById<View>(R.id.testAllowTaskReparent).setOnClickListener {
            startActivity(Intent(this, AllowTaskReparentingActivity::class.java))
        }
        findViewById<View>(R.id.testExcludeFromRecents).setOnClickListener {
            startActivity(Intent(this, ExcludeActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            })
        }

    }
}
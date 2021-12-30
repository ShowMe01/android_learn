package com.example.helloworld.lua

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.helloworld.R
import com.immomo.mls.InitData
import com.immomo.mls.MLSInstance

class LuaActivity : AppCompatActivity() {

    private lateinit var instance: MLSInstance
    private val entryPath = "file://android_asset/hello.lua"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lua)

        instance = MLSInstance(this, true, true)
        instance.setContainer(findViewById(R.id.root_view))
        val initData = InitData(entryPath)
        instance.setData(initData)

    }

    override fun onResume() {
        super.onResume()
        instance.onResume()
    }

    override fun onPause() {
        super.onPause()
        instance.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        instance.onDestroy()
    }

}
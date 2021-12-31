package com.example.helloworld.lua

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.helloworld.R
import com.example.helloworld.lua.utils.LuaUtils
import com.immomo.luanative.hotreload.HotReloadServer
import com.immomo.mls.HotReloadHelper
import com.immomo.mls.InitData
import com.immomo.mls.MLSInstance
import java.io.File

class LuaActivity : AppCompatActivity() {

    private lateinit var instance: MLSInstance
    private val entryPath = "file://android_asset/hello.lua"

    private val TAG = "LuaActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lua)

        instance = MLSInstance(this, true, true)
        instance.setContainer(findViewById(R.id.root_view))
        val initData = InitData(entryPath)
        instance.setData(initData)
        if (!instance.isValid) {
            Toast.makeText(this, "lua 非法url", Toast.LENGTH_LONG).show()
        }

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
package com.example.helloworld.lua.utils

import android.util.Log
import com.example.helloworld.application.MyApplication
import java.io.File

object LuaUtils {

    private fun getFileDir(): File = MyApplication.getInstance().filesDir

    const val TAG = "LuaUtils"

    fun getLuaSdDir(): String = getFileDir().path

    fun getLuaRootDir(): String {
        val path = File(getLuaSdDir(), "luaRoot").path
        Log.d(TAG, "getLuaRootDir: $path")
        return path
    }

    fun getLuaImgDir(): String {
        val path = File(getLuaRootDir(), "luaImg").path
        Log.d(TAG, "getLuaImgDir: $path")
        return path
    }

    fun getLuaCacheDir(): String {
        val path = File(getLuaRootDir(), "luaCache").path
        Log.d(TAG, "getLuaCacheDir: $path")
        return path
    }

    fun getLuaResDir(): String {
        val path = File(getLuaRootDir(), "luaRes").path
        Log.d(TAG, "getLuaResDir: $path")
        return path
    }


}
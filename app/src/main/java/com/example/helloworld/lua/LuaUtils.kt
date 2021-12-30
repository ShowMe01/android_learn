package com.example.helloworld.lua

import com.example.helloworld.application.MyApplication
import java.io.File

object LuaUtils {

    private fun getFileDir(): File = MyApplication.getInstance().filesDir

    fun getLuaSdDir(): String = getFileDir().path

    fun getLuaRootDir(): String = File(getLuaSdDir(), "luaRoot").path

    fun getLuaImgDir(): String = File(getLuaRootDir(), "luaImg").path

    fun getLuaCacheDir(): String = File(getLuaRootDir(), "luaCache").path

    fun getLuaResDir(): String = File(getLuaRootDir(), "luaRes").path


}
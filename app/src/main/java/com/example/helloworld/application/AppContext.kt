package com.example.helloworld.application

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object AppContext {

    private lateinit var sContext: Context

    fun init(context: Context) {
        this.sContext = context
    }

    fun getAppContext() : Context {
        return sContext
    }
}
package com.example.helloworld.util

import android.os.Handler
import android.os.Looper

class MainThreadExecutor private constructor() {

    private val handler by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        Handler(Looper.getMainLooper())
    }

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { MainThreadExecutor() }
    }

    fun post(r: Runnable) {
        handler.post(r)
    }

    fun postDelay(r: Runnable, delay: Long) {
        handler.postDelayed(r, delay)
    }

}
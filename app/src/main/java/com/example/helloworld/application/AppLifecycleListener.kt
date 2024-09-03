package com.example.helloworld.application

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.example.helloworld.cmb.XMPPManager

object AppLifecycleListener {

    private var startedActivityCount = 0
    private var isActivityChangingConfigurations = false
    private var isFront = false


    private val activityCallbacks = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        }

        override fun onActivityStarted(activity: Activity) {
            startedActivityCount++
            if (startedActivityCount == 1 && !isActivityChangingConfigurations) {
                // App进入前台
                onAppForegrounded()
            }
        }

        override fun onActivityResumed(activity: Activity) {
        }

        override fun onActivityPaused(activity: Activity) {
        }

        override fun onActivityStopped(activity: Activity) {
            isActivityChangingConfigurations = activity.isChangingConfigurations
            if (startedActivityCount > 0) {
                startedActivityCount--
            }
            if (startedActivityCount == 0 && !isActivityChangingConfigurations) {
                // App进入后台
                onAppBackgrounded()
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityDestroyed(activity: Activity) {
        }
    }

    private fun onAppForegrounded() {
        // 处理应用进入前台的逻辑
        Log.d("MyActivityLifecycle", "App进入前台")
        isFront = true
        XMPPManager.onAppFront()
    }

    private fun onAppBackgrounded() {
        // 处理应用进入后台的逻辑
        Log.d("MyActivityLifecycle", "App进入后台")
        isFront = false
    }

    fun isAppFront() = isFront

    fun register(application: Application) {
        application.registerActivityLifecycleCallbacks(activityCallbacks)
    }

}
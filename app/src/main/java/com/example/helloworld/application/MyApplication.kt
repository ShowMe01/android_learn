package com.example.helloworld.application

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.example.helloworld.application.AppContext.getAppContext
import com.tencent.mmkv.MMKV

class MyApplication : Application() {
    private val TAG = "MyApplication"


    override fun onCreate() {
        super.onCreate()
        AppContext.init(this)

        MMKV.initialize(getAppContext())
        AppLifecycleListener.register(this)
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
                Log.d(TAG, "onActivityResumed: activity: " + activity.javaClass.name)
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }
        })
    }

    companion object {
        @JvmStatic
        val instance: Context
            get() = getAppContext()

        @JvmStatic
        val packageNameImpl: String
            get() {
                var sPackageName = getAppContext().packageName
                if (sPackageName.contains(":")) {
                    sPackageName = sPackageName.substring(0, sPackageName.lastIndexOf(":"))
                }
                return sPackageName
            }
    }
}

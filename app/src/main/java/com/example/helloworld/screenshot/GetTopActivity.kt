package com.example.helloworld.screenshot

import android.app.ActivityManager
import android.app.ActivityManager.RunningTaskInfo
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.helloworld.R
import kotlin.concurrent.thread


class GetTopActivity : AppCompatActivity() {

    private var isResume = false

    private val TAG = "GetTopActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_top)

        thread {
            val mActivityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
            while (true) {
                Thread.sleep(1000)
                try {
                    val rtis: List<RunningTaskInfo> = mActivityManager.getRunningTasks(1)
                    val packageName = rtis[0].topActivity?.packageName
                    val topActivityName = rtis[0].topActivity?.className
                    val applicationName =
                        AppInfoUtil.getApplicationName(this, packageName.safe())

                    Log.d(
                        TAG,
                        "package : $packageName, topActivityName:$topActivityName , applicationName: $applicationName"
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    break
                }
            }
            Log.d(TAG, "PackageInfo Monitor exit ")
        }
    }

    override fun onResume() {
        isResume = true
        super.onResume()
    }

    override fun onPause() {
        isResume = false
        super.onPause()
    }
}

fun String?.safe(): String = this ?: ""
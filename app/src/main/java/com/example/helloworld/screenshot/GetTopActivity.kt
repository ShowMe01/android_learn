package com.example.helloworld.screenshot

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.ActivityManager.RunningTaskInfo
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.CompoundButton
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.example.helloworld.R
import kotlin.concurrent.thread


class GetTopActivity : AppCompatActivity() {

    private var isResume = false

    private val TAG = "GetTopActivity"

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_top)

        val switch = findViewById<Switch>(R.id.topSwitch)
        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if (!MyAccessibilityService.hasConnect) {
                    AlertDialog.Builder(this)
                        .setMessage("打开我的 \"辅助功能\" 后才能用哦.")
                        .setPositiveButton("去设置") { dialog, which ->
                            val intent = Intent()
                            intent.action = Settings.ACTION_ACCESSIBILITY_SETTINGS
                            startActivity(intent)
                        }
                        .setNegativeButton("取消") { dialog, which -> }
                        .create()
                        .show()
                } else {
                    loopLogTopActivity()
                }
                switch.isChecked = MyAccessibilityService.hasConnect
            }
        }
    }

    private fun loopLogTopActivity() {
        thread {
            val mActivityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
            while (true) {
                Thread.sleep(1000)
                try {
                    val rtis: List<RunningTaskInfo> =
                        mActivityManager.getRunningTasks(1)
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
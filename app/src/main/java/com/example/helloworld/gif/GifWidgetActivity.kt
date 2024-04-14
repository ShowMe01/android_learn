package com.example.helloworld.gif

import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.helloworld.R
import java.io.File
import java.util.*

class GifWidgetActivity : AppCompatActivity() {
    var curIndex = 0
//    var imgList = arrayOf(R.drawable.number_1, R.drawable.number_2, R.drawable.number_3)
    private var timer = Timer()
    var realCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gif_widget)

//        val appWidgetManager = AppWidgetManager.getInstance(this)
//        val widgetIds = appWidgetManager
//            .getAppWidgetIds(
//                ComponentName(
//                    this,
//                    GifWidget::class.java
//                )
//            )
//
//        timer.schedule(object : TimerTask() {
//            override fun run() {
//                for (widgetId in widgetIds) {
//                    val views = RemoteViews(packageName, R.layout.gif_widget)
//                    views.setImageViewResource(R.id.iv_gif, imgList[curIndex])
//                    appWidgetManager.partiallyUpdateAppWidget(widgetId, views)
//                }
//                Log.d("testGif", "run: $curIndex  realCount: ${realCount++}")
//                curIndex = (curIndex + 1) % imgList.size
//            }
//        }, 2000, 16)

//        countDownTimer.start()

        testPath()
    }

    private fun testPath(){
        val albumFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val videoPath = File(albumFile, "Camera/VID_20220323_203559.mp4")

        Log.d("testPath", " videoPath: $videoPath")
    }

}
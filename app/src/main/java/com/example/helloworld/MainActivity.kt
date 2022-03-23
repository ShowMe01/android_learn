package com.example.helloworld

import android.Manifest
import android.app.NotificationChannelGroup
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.BinderThread
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.helloworld.gif.GifWidgetActivity
import com.example.helloworld.lua.LuaActivity
import com.example.helloworld.notification.NotificationUtil

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        testLua()
        testGifWidget()
//        testNotification()
    }

    private fun testGifWidget() {
        findViewById<Button>(R.id.btn_gif_widget).setOnClickListener {
            startActivity(Intent(this, GifWidgetActivity::class.java))
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun testLua() {

        //请求读写权限
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                1
            )
        }

        findViewById<View>(R.id.btn_lua).setOnClickListener {
            startActivity(Intent(this, LuaActivity::class.java))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun testNotification() {
        findViewById<View>(R.id.rl_container).postDelayed(Runnable {

            val nm = NotificationManagerCompat.from(this)
            //注册通知渠道分组，在创建通道之前
            nm.createNotificationChannelGroup(
                NotificationChannelGroup(
                    NotificationUtil.MSG_GROUP_ID,
                    NotificationUtil.MSG_GROUP_NAME
                )
            )

            nm.createNotificationChannelGroup(
                NotificationChannelGroup(
                    NotificationUtil.OPPO_GROUP_ID,
                    NotificationUtil.OPPO_GROUP_NAME
                )
            )

            //注册通知渠道
            NotificationUtil.createNotificationChannel(
                this,
                NotificationUtil.MSG_CHANNEL_ID,
                NotificationUtil.MSG_CHANNEL_NAME,
                NotificationUtil.MSG_CHANNEL_DESCRIPTION,
                NotificationUtil.MSG_GROUP_ID
            )
            NotificationUtil.createNotificationChannel(
                this,
                NotificationUtil.OPPO_CHANNEL_ID,
                NotificationUtil.OPPO_CHANNEL_NAME,
                NotificationUtil.OPPO_CHANNEL_DESCRIPTION,
                NotificationUtil.OPPO_GROUP_ID
            )

            //发通知
            val msgNotification = NotificationUtil.createNotification(
                this,
                NotificationUtil.MSG_CHANNEL_ID,
                "新消息",
                "你好，在吗?",
            )
            nm.notify(666, msgNotification)

            val oppoNotification = NotificationUtil.createNotification(
                this,
                NotificationUtil.OPPO_CHANNEL_ID,
                "OPPO",
                "oppo测试渠道",
            )
            nm.notify(778, oppoNotification)

        }, 2000L)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_item -> {
                Toast.makeText(baseContext, "addItem", Toast.LENGTH_LONG).show()
            }
            R.id.remove_item -> {
                Toast.makeText(baseContext, "remove_item", Toast.LENGTH_LONG).show()
            }
            R.id.more_1 -> {
                Toast.makeText(baseContext, "more_1", Toast.LENGTH_LONG).show()
            }
            R.id.more_2 -> {
                Toast.makeText(baseContext, "more_2", Toast.LENGTH_LONG).show()
            }
        }
        return true
    }
}
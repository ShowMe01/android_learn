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
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.helloworld.chat.ChatClientActivity
import com.example.helloworld.chat.ChatServerActivity
import com.example.helloworld.constraint.ConstraintActivity
import com.example.helloworld.databinding.ActivityMainBinding
import com.example.helloworld.gif.GifWidgetActivity
import com.example.helloworld.ime.ImeActivity
import com.example.helloworld.ipc.TestIPCActivity
import com.example.helloworld.launchmode.StartActivity
import com.example.helloworld.layoutinflater.LayoutInflaterActivity
import com.example.helloworld.lua.LuaActivity
import com.example.helloworld.media.MediaActivity
import com.example.helloworld.notification.NotificationUtil
import com.example.helloworld.rv.CoverFlowActivity
import com.example.helloworld.rv.RvActivity
import com.example.helloworld.screenshot.GetTopActivity
import com.example.helloworld.share.TestShareActivity
import com.example.helloworld.web.WebActivity
import com.example.helloworld.windowinsets.WindowInsetsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)
//         Configure the behavior of the hidden system bars.
        windowInsetsController?.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController?.hide(WindowInsetsCompat.Type.statusBars());


        testLua()
        testGifWidget()
        testConstraintLayout()
        testNotification()

        viewBinding.btnMedia.setOnClickListener {
            startActivity(Intent(this, MediaActivity::class.java))
        }

        viewBinding.windowInsets.setOnClickListener {
            startActivity(Intent(this, WindowInsetsActivity::class.java))
        }
        viewBinding.btnTopApp.setOnClickListener {
            startActivity(Intent(this, GetTopActivity::class.java))
        }

        viewBinding.btnChat.setOnClickListener {
            startActivity(Intent(this, ChatClientActivity::class.java))
        }
        findViewById<View>(R.id.btn_web).setOnClickListener {
            startActivity(Intent(this, WebActivity::class.java))
        }
        findViewById<View>(R.id.btnIme).setOnClickListener {
            startActivity(Intent(this, ImeActivity::class.java))
        }

        findViewById<View>(R.id.btnShare).setOnClickListener {
            startActivity(Intent(this, TestShareActivity::class.java))
        }

        viewBinding.btnLayoutInflater.setOnClickListener {
            startActivity(Intent(this, LayoutInflaterActivity::class.java))
        }

        findViewById<View>(R.id.btnRv).setOnClickListener {
            startActivity(Intent(this, RvActivity::class.java))
        }

        viewBinding.btnChatServer.setOnClickListener {
            startActivity(Intent(this, ChatServerActivity::class.java))
        }

        viewBinding.btnGallery.setOnClickListener {
            startActivity(Intent(this, CoverFlowActivity::class.java))
        }
        viewBinding.btnLaunchMode.setOnClickListener {
            startActivity(Intent(this, StartActivity::class.java))
        }
        viewBinding.btnIPC.setOnClickListener {
            startActivity(Intent(this, TestIPCActivity::class.java))
        }
    }

    private fun testConstraintLayout() {
        findViewById<View>(R.id.btn_constraints).setOnClickListener {
            startActivity(Intent(this, ConstraintActivity::class.java))
        }
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
        findViewById<View>(R.id.rl_container).postDelayed({

            val nm = NotificationManagerCompat.from(this)
            //注册通知渠道分组，在创建通道之前
            nm.createNotificationChannelGroup(
                NotificationChannelGroup(
                    NotificationUtil.MSG_GROUP_ID,
                    NotificationUtil.MSG_GROUP_NAME
                )
            )

            /*nm.createNotificationChannelGroup(
                NotificationChannelGroup(
                    NotificationUtil.OPPO_GROUP_ID,
                    NotificationUtil.OPPO_GROUP_NAME
                )
            )*/

            //注册通知渠道
            NotificationUtil.createNotificationChannel(
                this,
                NotificationUtil.MSG_CHANNEL_ID,
                NotificationUtil.MSG_CHANNEL_NAME,
                NotificationUtil.MSG_CHANNEL_DESCRIPTION,
                NotificationUtil.MSG_GROUP_ID
            )
            /* NotificationUtil.createNotificationChannel(
                 this,
                 NotificationUtil.OPPO_CHANNEL_ID,
                 NotificationUtil.OPPO_CHANNEL_NAME,
                 NotificationUtil.OPPO_CHANNEL_DESCRIPTION,
                 NotificationUtil.OPPO_GROUP_ID
             )*/

            //发通知
            val msgNotification = NotificationUtil.createNotification(
                this,
                NotificationUtil.MSG_CHANNEL_ID,
                "新消息",
                "你好，在吗?",
            )
            nm.notify(666, msgNotification)

            /*val oppoNotification = NotificationUtil.createNotification(
                this,
                NotificationUtil.OPPO_CHANNEL_ID,
                "OPPO",
                "oppo测试渠道",
            )
            nm.notify(778, oppoNotification)
             */

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
package com.example.helloworld.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.example.helloworld.R
import com.example.helloworld.databinding.ActivityChatBinding
import com.example.helloworld.util.MainThreadExecutor
import dalvik.system.InMemoryDexClassLoader
import java.io.*
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.util.concurrent.Semaphore
import kotlin.concurrent.thread
import kotlin.math.log

class ChatClientActivity : AppCompatActivity() {

    private val TAG = "ChatActivity"
    private lateinit var viewBinding: ActivityChatBinding
    private val msgBuilder = StringBuilder("")

    private var clientSocket: Socket? = null
    private val semaphore = Semaphore(0)

    companion object {
        const val port = 9898
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initClient()
        initView()
    }

    private fun initClient() {
        thread {
            // 连接端口
            semaphore.acquire()
            Log.d(TAG, "initClient: before client socket")
            val socket = Socket("172.16.232.254", port)
            Log.d(TAG, "initClient: after client socket")
            clientSocket = socket
            val inputStream = socket.getInputStream()
            // 循环监听端口信息
            while (true) {
                val reader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
                val msg = reader.readLine()
                Log.d(TAG, "initClient: client receive msg:${msg}")
                if (msg != null) {
                    MainThreadExecutor.instance.post {
                        // 更新到view上
                        viewBinding.msgList.append("\n$msg")
                    }
                }
            }
        }
    }

    private fun initView() {
        viewBinding.btnSend.setOnClickListener {
            // 发送消息到服务器
            val msg = viewBinding.inputPanel.text.toString()
            if (!TextUtils.isEmpty(msg)) {
                thread {
                    clientSocket?.let { socket ->
                        val outputStream = socket.getOutputStream()
                        val writer = BufferedWriter(
                            OutputStreamWriter(
                                outputStream,
                                StandardCharsets.UTF_8
                            )
                        )
                        writer.write("$msg\n")
                        writer.flush()
                        MainThreadExecutor.instance.post {
                            viewBinding.inputPanel.setText("")
                        }
                    }
                }
            }
        }
    }
}
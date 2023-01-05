package com.example.helloworld.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.helloworld.R
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.StandardCharsets
import kotlin.concurrent.thread

class ChatServerActivity : AppCompatActivity() {

    private val TAG = "ChatServerActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_server)
        initServer()
    }

    /**
     * 初始化服务器
     */
    private fun initServer() {
        thread {
            val localHost = InetAddress.getLocalHost()
            Log.d(TAG, "localHost:$localHost")
            // 监听端口
            Log.d(TAG, "initServer: before server socket ")
            val serverSocket = ServerSocket(ChatClientActivity.port)
            Log.d(TAG, "initServer: after server socket")
            val clients = mutableListOf<Socket>()
            while (true) {
                val socket = serverSocket.accept()
                clients.add(socket)
                bindClientMsg(socket, clients)
            }
        }
    }



    /**
     * 不断获取来自客户端消息，转发给所有客户端
     */
    private fun bindClientMsg(
        socket: Socket,
        clients: MutableList<Socket>
    ) {
        thread {
            val inputStream = socket.getInputStream()
            val reader = BufferedReader(InputStreamReader(inputStream))
            while (true) {
                val msg = reader.readLine()
                Log.d(TAG, "bindClientMsg server receive msg:${msg}")
                if (msg != null) {
                    try {
                        for (client in clients) {
                            val os = client.getOutputStream()
                            val writer =
                                BufferedWriter(OutputStreamWriter(os, StandardCharsets.UTF_8))
                            writer.write("$msg\n")
                            writer.flush()
                        }
                    } catch (e: Exception) {
                        //可能有并发修改问题，先忽略
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}
package com.example.helloworld.cmb

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.helloworld.R
import com.example.helloworld.base.BaseViewBindingActivity
import com.example.helloworld.databinding.ActivityCmbChatBinding

class CMBChatActivity : BaseViewBindingActivity<ActivityCmbChatBinding>() {

    companion object {
        const val KEY_USER_ID = "KEY_USER_ID"

        @JvmStatic
        fun start(context: Context, username: String) {
            val starter = Intent(context, CMBChatActivity::class.java).apply {
                putExtra(KEY_USER_ID, username)
            }
            context.startActivity(starter)
        }
    }

    private val to = "alice@chatdev.moond4rk.com"

    private lateinit var recyclerView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter

    override fun init() {
        recyclerView = findViewById(R.id.rv)
        messageAdapter = MessageAdapter(mutableListOf())
        recyclerView.adapter = messageAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        XMPPManager.addIncomingMessageListener { from, message, chat ->
            runOnUiThread {
                messageAdapter.addMessage(MessageItem(message.body, false))
                recyclerView.scrollToPosition(messageAdapter.itemCount - 1)
            }
        }

        viewBinding.sendButton.setOnClickListener {
            val messageText = viewBinding.messageEditText.text.toString()
            if (messageText.isNotEmpty()) {
                XMPPManager.sendMessage(
                    to,
                    messageText
                ) { success, error ->
                    if (success) {
                        runOnUiThread {
                            messageAdapter.addMessage(MessageItem(messageText, true))
                            recyclerView.scrollToPosition(messageAdapter.itemCount - 1)
                            viewBinding.messageEditText.text.clear()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this,
                                "Failed to send message: $error",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
        // 发送消息
    }

    override fun viewBinding(): ActivityCmbChatBinding {
        return ActivityCmbChatBinding.inflate(layoutInflater)
    }

    override fun onDestroy() {
        super.onDestroy()
        XMPPManager.disconnect()
    }
}
package com.example.helloworld.cmb

import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.helloworld.base.BaseViewBindingActivity
import com.example.helloworld.databinding.ActivityCmbChatBinding
import com.example.helloworld.util.dp

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

    private lateinit var messageAdapter: MessageAdapter

    override fun init() {
        messageAdapter = MessageAdapter(mutableListOf())
        viewBinding.rv.adapter = messageAdapter
        viewBinding.rv.layoutManager = LinearLayoutManager(this)

        setUpIme()

        XMPPManager.addIncomingMessageListener { from, message, chat ->
            runOnUiThread {
                messageAdapter.addMessage(MessageItem(message.body, false))
                viewBinding.rv.scrollToPosition(messageAdapter.itemCount - 1)
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
                            viewBinding.rv.scrollToPosition(messageAdapter.itemCount - 1)
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

    private fun setUpIme() {
        val params = window.attributes
        params.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
        window.attributes = params
        WindowCompat.setDecorFitsSystemWindows(window, false)

        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.root) { view, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val imeHeight = imeInsets.bottom



            if (imeVisible && imeHeight > 0) {
                viewBinding.imePlaceHolder.updateLayoutParams<ViewGroup.LayoutParams> {
                    height = imeHeight + 1.dp.toInt()
                }
                viewBinding.rv.post {
                    viewBinding.rv.scrollToPosition(messageAdapter.itemCount - 1)
                }
            }
            viewBinding.imePlaceHolder.isVisible = imeVisible && imeHeight > 0

            insets
        }
    }

    override fun viewBinding(): ActivityCmbChatBinding {
        return ActivityCmbChatBinding.inflate(layoutInflater)
    }

    override fun onDestroy() {
        super.onDestroy()
        XMPPManager.disconnect()
    }
}
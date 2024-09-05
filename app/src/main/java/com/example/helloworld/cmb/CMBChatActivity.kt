package com.example.helloworld.cmb

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.MenuItem
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

    private val to = "alice"

    private lateinit var messageAdapter: MessageAdapter

    override fun init() {
        messageAdapter = MessageAdapter(mutableListOf())
        vb.rv.adapter = messageAdapter
        vb.rv.layoutManager = LinearLayoutManager(this)

        setUpIme()
        initToolBar()


        XMPPManager.addIncomingMessageListener { from, message, chat ->
            Log.d(XMPPManager.TAG, "init: msgId:${message.stanzaId}")
            runOnUiThread {
                messageAdapter.addMessage(MessageItem(message.body, false))
                vb.rv.scrollToPosition(messageAdapter.itemCount - 1)
            }
        }


        vb.sendButton.setOnClickListener {
            val messageText = vb.messageEditText.text.toString()
            if (messageText.isNotEmpty()) {
                XMPPManager.sendMessage(
                    getJid(),
                    messageText
                ) { success, error ->
                    if (success) {
                        runOnUiThread {
                            messageAdapter.addMessage(MessageItem(messageText, true))
                            vb.rv.scrollToPosition(messageAdapter.itemCount - 1)
                            vb.messageEditText.text.clear()
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

    private fun getJid(): String {
        return "${to}@${XMPPManager.DOMAIN}"
    }

    private fun initToolBar() {
        setSupportActionBar(vb.toolbar)
        vb.toolbarTitle.text = to
        vb.toolbarTitle.setOnClickListener {
            /*XMPPManager.fetchHistory(FetchMsgParams(getJid(),{

            }))*/
        }
        supportActionBar?.apply {
            title = ""
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }


            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    private fun setUpIme() {
        val params = window.attributes
        params.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
        window.attributes = params
        WindowCompat.setDecorFitsSystemWindows(window, false)

        ViewCompat.setOnApplyWindowInsetsListener(vb.root) { view, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val imeHeight = imeInsets.bottom



            if (imeVisible && imeHeight > 0) {
                vb.imePlaceHolder.updateLayoutParams<ViewGroup.LayoutParams> {
                    height = imeHeight + 1.dp.toInt()
                }
                vb.rv.post {
                    vb.rv.scrollToPosition(messageAdapter.itemCount - 1)
                }
            }
            vb.imePlaceHolder.isVisible = imeVisible && imeHeight > 0

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
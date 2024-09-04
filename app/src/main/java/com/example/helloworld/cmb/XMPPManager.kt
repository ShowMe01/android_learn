package com.example.helloworld.cmb

import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.example.helloworld.util.MainThreadExecutor
import org.jivesoftware.smack.AbstractXMPPConnection
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.ConnectionListener
import org.jivesoftware.smack.SmackConfiguration
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.chat2.IncomingChatMessageListener
import org.jivesoftware.smack.debugger.SmackDebugger
import org.jivesoftware.smack.debugger.SmackDebuggerFactory
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.TopLevelStreamElement
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jivesoftware.smackx.ping.PingManager
import org.jxmpp.jid.EntityFullJid
import org.jxmpp.jid.impl.JidCreate

object XMPPManager {

    const val TAG = "XMPPManager"
    private var connection: AbstractXMPPConnection? = null

    private var state = ConnectionState.CLOSED

    const val HOST = "chatdev.moond4rk.com"
    const val DOMAIN = "chatdev.moond4rk.com"

    private val handlerThread = HandlerThread("XMPP-thread").also {
        it.start()
    }


    private val handler = Handler(handlerThread.looper) { msg ->
        when (msg.what) {
            CONNECT -> {
                val data = msg.obj as ConnectParams
                connectInternal(
                    data.username,
                    data.password,
                    data.domain,
                    data.host,
                    data.port,
                    data.callback
                )
            }

            DISCONNECT -> {
                disconnectInternal()
            }

            SEND_MESSAGE -> {
                val data = msg.obj as SendMessageParams
                sendMessageInternal(data.to, data.messageBody, data.callback)
            }

            TRY_RECONNECT -> {
                tryReconnectInternal()
            }
        }
        true
    }

    private const val CONNECT = 1
    private const val DISCONNECT = 2
    private const val SEND_MESSAGE = 3
    private const val TRY_RECONNECT = 4

    private val connectionListener = object : ConnectionListener {
        override fun connected(connection: XMPPConnection?) {
            Log.d(TAG, "ConnectionListener connected: ")
            state = ConnectionState.CONNECTED
        }

        override fun connecting(connection: XMPPConnection?) {
            Log.d(TAG, "ConnectionListener connecting: ")
//            state = ConnectionState.CONNECTING
        }

        override fun authenticated(connection: XMPPConnection?, resumed: Boolean) {
            Log.d(TAG, "ConnectionListener authenticated: ")
            state = ConnectionState.AUTHENTICATED
        }

        override fun connectionClosed() {
            Log.d(TAG, "ConnectionListener connectionClosed: ")
            state = ConnectionState.CLOSED
        }

        override fun connectionClosedOnError(e: java.lang.Exception?) {
            Log.d(TAG, "ConnectionListener connectionClosedOnError:  ${e}")
            state = ConnectionState.ERROR_CLOSED
            tryReconnect()
        }

    }

    val debugger = object : SmackDebuggerFactory {
        override fun create(p0: XMPPConnection?): SmackDebugger {
            return object : SmackDebugger(p0) {
                override fun userHasLogged(p0: EntityFullJid?) {
                    Log.d(TAG, "userHasLogged: ${p0}")
                }

                override fun outgoingStreamSink(p0: CharSequence?) {
                    Log.d(TAG, "outgoingStreamSink: ${p0}")
                }

                override fun incomingStreamSink(p0: CharSequence?) {
                    Log.d(TAG, "incomingStreamSink: ${p0}")

                }

                override fun onIncomingStreamElement(p0: TopLevelStreamElement?) {
                    Log.d(TAG, "onIncomingStreamElement: ${p0}")

                }

                override fun onOutgoingStreamElement(p0: TopLevelStreamElement?) {
                    Log.d(TAG, "onOutgoingStreamElement: ${p0}")
                }

            }
        }

    }

    fun connect(
        username: String,
        password: String,
        domain: String,
        host: String,
        port: Int,
        callback: (Boolean, String?) -> Unit
    ) {
        val msg = handler.obtainMessage(
            CONNECT,
            ConnectParams(username, password, domain, host, port, callback)
        )
        handler.sendMessage(msg)
    }


    private fun connectInternal(
        username: String,
        password: String,
        domain: String,
        host: String,
        port: Int,
        callback: (Boolean, String?) -> Unit
    ) {
        try {
            SmackConfiguration.DEBUG = true
            val config =
                XMPPTCPConnectionConfiguration.builder()
                    .setXmppDomain(domain)
                    .setHost(host)
                    .setPort(port)
                    .setResource("Android")
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .setUsernameAndPassword(username, password)
                    .setDebuggerFactory(debugger)
                    .build()

            Log.d(TAG, "connect: before connect")

            connection = XMPPTCPConnection(config).apply {
                addConnectionListener(connectionListener)
            }
            connectAndLogin()

            MainThreadExecutor.instance.post {
                callback(true, null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "connect: error :${e}")
            MainThreadExecutor.instance.post {
                callback(false, e.message)
            }
        }
    }


    private fun connectAndLogin() {
        connection?.connect()
        Log.d(TAG, "after connect: ")
        connection?.login()
        Log.d(TAG, "after login : ")

        PingManager.getInstanceFor(connection).apply {
            pingInterval = 60
            pingServerIfNecessary()
            registerPingFailedListener {
                Log.e(TAG, "PingManager : pingFailed")
                disconnect()
            }
        }
    }

    fun disconnect() {
        handler.sendEmptyMessage(DISCONNECT)
    }

    private fun disconnectInternal() {
        connection?.removeConnectionListener(connectionListener)
        connection?.disconnect()
    }

    private fun sendMessageInternal(
        to: String,
        messageBody: String,
        callback: (Boolean, String?) -> Unit
    ) {
        try {
            val chatManager = ChatManager.getInstanceFor(connection)
            val jid = JidCreate.entityBareFrom(to)
            val chat = chatManager.chatWith(jid)
            val message = Message(jid, Message.Type.chat)
            message.body = messageBody
            chat.send(message)
            MainThreadExecutor.instance.post {
                callback(true, null)
            }
        } catch (e: SmackException.NotConnectedException) {
            MainThreadExecutor.instance.post {
                callback(false, "Not connected")
            }
        } catch (e: Throwable) {
            MainThreadExecutor.instance.post {
                callback(false, e.message)
            }
        }
    }

    fun sendMessage(to: String, messageBody: String, callback: (Boolean, String?) -> Unit) {
        val msg = handler.obtainMessage(SEND_MESSAGE, SendMessageParams(to, messageBody, callback))
        handler.sendMessage(msg)
    }

    fun addIncomingMessageListener(listener: IncomingChatMessageListener) {
        connection?.let {
            val chatManager = ChatManager.getInstanceFor(it)
            chatManager.addIncomingListener(listener)
        }
    }

    fun onAppFront() {
        Log.d(TAG, "onAppFront: ")
        tryReconnect()
    }

    fun onNetworkAvailable() {
        Log.d(TAG, "onNetworkAvailable: ")
        tryReconnect()
    }

    private fun tryReconnectInternal() {
        val needReconnect =
            state == ConnectionState.ERROR_CLOSED && connection?.isConnected == false
        Log.d(TAG, "tryReconnect: needReconnect: ${needReconnect}")
        if (needReconnect) {
            try {
                connectAndLogin()
            } catch (e: Throwable) {
                Log.e(TAG, "reconnect: ${e}")

            }
        }
    }

    fun tryReconnect() {
        handler.sendEmptyMessage(TRY_RECONNECT)
    }
}
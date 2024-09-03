package com.example.helloworld.cmb

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jivesoftware.smack.AbstractXMPPConnection
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.ConnectionListener
import org.jivesoftware.smack.ReconnectionManager
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
import org.jxmpp.jid.EntityFullJid
import org.jxmpp.jid.impl.JidCreate

object XMPPManager {

    const val TAG = "XMPPManager"
    private var connection: AbstractXMPPConnection? = null

    fun connect(
        username: String,
        password: String,
        domain: String,
        host: String,
        port: Int,
        callback: (Boolean, String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                SmackConfiguration.DEBUG = true
//                SmackConfiguration.addSaslMech(SASLPlainMechanism.NAME)
                val config =
                    XMPPTCPConnectionConfiguration.builder()
                        .setXmppDomain(domain)
                        .setHost(host)
                        .setPort(port)
                        .setResource("Android")
                        .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                        .setUsernameAndPassword(username, password)
                        .setDebuggerFactory(object : SmackDebuggerFactory {
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

                        })
                        .build()

                Log.d(TAG, "connect: before connect")
                ReconnectionManager.getInstanceFor(connection).setFixedDelay(10)
                connection = XMPPTCPConnection(config).apply {
                    addConnectionListener(object : ConnectionListener {
                        override fun connected(connection: XMPPConnection?) {
                            super.connected(connection)
                            Log.d(TAG, "ConnectionListener connected: ")
                        }

                        override fun connecting(connection: XMPPConnection?) {
                            super.connecting(connection)
                            Log.d(TAG, "ConnectionListener connecting: ")

                        }

                        override fun authenticated(connection: XMPPConnection?, resumed: Boolean) {
                            super.authenticated(connection, resumed)
                            Log.d(TAG, "ConnectionListener authenticated: ")
                        }

                        override fun connectionClosed() {
                            super.connectionClosed()
                            Log.d(TAG, "ConnectionListener connectionClosed: ")
                        }

                        override fun connectionClosedOnError(e: java.lang.Exception?) {
                            super.connectionClosedOnError(e)
                            Log.d(TAG, "ConnectionListener connectionClosedOnError:  ${e}")
                        }
                    })
                }
                connection?.connect()
                Log.d(TAG, "after connect: ")
                connection?.login()
                Log.d(TAG, "after login : ")
                withContext(Dispatchers.Main) {
                    callback(true, null)
                }
            } catch (e: Exception) {
                Log.e(TAG, "connect: error :${e}")
                withContext(Dispatchers.Main) {
                    callback(false, e.message)
                }
            }
        }
    }

    fun disconnect() {
        CoroutineScope(Dispatchers.IO).launch {
            connection?.disconnect()
        }
    }

    fun sendMessage(to: String, messageBody: String, callback: (Boolean, String?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val chatManager = ChatManager.getInstanceFor(connection)
                val jid = JidCreate.entityBareFrom(to)
                val chat = chatManager.chatWith(jid)
                val message = Message(jid, Message.Type.chat)
                message.body = messageBody
                chat.send(message)
                withContext(Dispatchers.Main) {
                    callback(true, null)
                }
            } catch (e: SmackException.NotConnectedException) {
                withContext(Dispatchers.Main) {
                    callback(false, "Not connected")
                }
            } catch (e: Throwable) {
                withContext(Dispatchers.Main) {
                    callback(false, e.message)
                }
            }
        }
    }

    fun isConnected(): Boolean = connection?.isConnected ?: false

    fun isAuthenticated(): Boolean = connection?.isAuthenticated ?: false
    fun addIncomingMessageListener(listener: IncomingChatMessageListener) {
        connection?.let {
            val chatManager = ChatManager.getInstanceFor(it)
            chatManager.addIncomingListener(listener)
        }
    }
}
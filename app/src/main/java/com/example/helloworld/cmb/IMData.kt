package com.example.helloworld.cmb

import org.jivesoftware.smack.packet.Message

data class MessageItem(val message: String, val fromMe: Boolean)

enum class ConnectionState {
    CLOSED,
    CONNECTING,
    CONNECTED,
    AUTHENTICATED,
    ERROR_CLOSED
}

data class ConnectParams(
    val username: String,
    val password: String,
    val domain: String,
    val host: String,
    val port: Int,
    val callback: (Boolean, String?) -> Unit
)

 data class SendMessageParams(
    val to: String,
    val messageBody: String,
    val callback: (Boolean, String?) -> Unit
)

data class FetchMsgParams(val jid: String, val callback: (List<Message>) -> Unit)
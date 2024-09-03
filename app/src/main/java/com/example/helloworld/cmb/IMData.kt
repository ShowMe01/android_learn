package com.example.helloworld.cmb

data class MessageItem(val message: String, val isFrom: Boolean)


enum class ConnectionState {

    CLOSED,
    CONNECTING,
    CONNECTED,
    AUTHENTICATED,
    ERROR_CLOSED
}
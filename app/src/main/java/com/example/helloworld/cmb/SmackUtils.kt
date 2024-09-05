package com.example.helloworld.cmb

import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.delay.packet.DelayInformation
import java.util.Date

object SmackUtils {

    fun getMsgTime(msg: Message):Date {
        val extension = msg.getExtension(DelayInformation.NAMESPACE) as DelayInformation
        return extension.stamp
    }
}
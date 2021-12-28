package com.example.helloworld.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.helloworld.R

object NotificationUtil {

    //<editor-fold desc="msg_notification_channel">
    const val MSG_CHANNEL_ID = "msg_channel_id"
    const val MSG_CHANNEL_NAME = "与你有关"
    const val MSG_CHANNEL_DESCRIPTION = "展示与你有关的通知"
    const val MSG_GROUP_ID = "msg_group_id"
    const val MSG_GROUP_NAME = "消息"
    //</editor-fold>

    //<editor-fold desc="oppo_notification_channel">
    const val OPPO_CHANNEL_ID = "oppo_channel_id"
    const val OPPO_CHANNEL_NAME = "OPPO通知渠道"
    const val OPPO_CHANNEL_DESCRIPTION = "OPPO通知渠道描述"
    const val OPPO_GROUP_ID = "oppo_group_id"
    const val OPPO_GROUP_NAME = "OPPO"
    //</editor-fold>


    fun createNotification(
        context: Context,
        channelId: String,
        contentTitle: String,
        contentText: String,
        groupId: String
    ): Notification {
        //点按通知操作，进入NotificationActivity
        val intent = Intent(context, NotificationActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_baseline_event_note_24)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setGroup(groupId)
            .build()
    }

    fun createNotificationChannel(
        context: Context,
        channelId: String,
        channelName: String,
        descriptionText: String,
        groupId: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = descriptionText
                group = groupId
            }

            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}
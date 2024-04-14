package com.example.helloworld.screenshot

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class MyAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "MyAccessibilityService"
        var hasConnect = false
    }

    /**
     * (2)配置的相关事件回调
     */
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
            Log.d(TAG, "onAccessibilityEvent: event: ${event.eventType}")
        }
    }

    /**
     * (3)当系统要中断服务正在提供的反馈（通常是为了响应将焦点移到其他控件等用户操作）时，会调用此方法。此方法可能会在服务的整个生命周期内被调用多次。
     */
    override fun onInterrupt() {
    }

    /**
     * （1）当系统成功连接到无障碍服务时，会调用此方法
     */
    override fun onServiceConnected() {
        super.onServiceConnected()
        hasConnect = true
        // 动态配置服务
        /*val info = AccessibilityServiceInfo().apply {
            // Set the type of events that this service wants to listen to. Others
            // won't be passed to this service.
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED

            // If you only want this service to work with specific applications, set their
            // package names here. Otherwise, when the service is activated, it will listen
            // to events from all applications.
            packageNames = arrayOf(packageName)

            // Set the type of feedback your service will provide.
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC

            // Default services are invoked only if no package-specific ones are present
            // for the type of AccessibilityEvent generated. This service *is*
            // application-specific, so the flag isn't necessary. If this was a
            // general-purpose service, it would be worth considering setting the
            // DEFAULT flag.

            flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS

            notificationTimeout = 100
        }
        this.serviceInfo = info*/
        Log.d(TAG, "onServiceConnected: hasConnect:${hasConnect}")
    }

    /**
     * (4) 服务关闭回调
     */
    override fun onUnbind(intent: Intent?): Boolean {
        hasConnect = false
        return super.onUnbind(intent)
    }
}
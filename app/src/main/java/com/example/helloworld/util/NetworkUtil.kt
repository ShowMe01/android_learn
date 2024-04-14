package com.example.helloworld.util

import android.content.Context
import android.net.wifi.WifiManager
import com.example.helloworld.application.AppContext

object NetworkUtil {

    fun getWifiIp() {
        val wifiManager = AppContext.getAppContext().getApplicationContext()
            .getSystemService(Context.WIFI_SERVICE) as WifiManager

    }

}
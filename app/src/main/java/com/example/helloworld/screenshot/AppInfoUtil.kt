package com.example.helloworld.screenshot

import android.content.Context


object AppInfoUtil {

    /**
     * get any application name by its package name
     * https://stackoverflow.com/questions/36590683/how-to-get-application-name-from-package-name-in-android
     * https://stackoverflow.com/questions/7802780/how-to-get-the-name-of-the-application-in-android
     * https://stackoverflow.com/questions/11229219/android-how-to-get-application-name-not-package-name
     */
    fun getApplicationName(context: Context, packageName: String): String {
        val packageManager = context.packageManager
        val info = packageManager.getApplicationInfo(packageName, 0)
        val stringId = info.labelRes
        return if (stringId == 0) info.nonLocalizedLabel.toString() else context.getString(
            stringId
        )
    }

}
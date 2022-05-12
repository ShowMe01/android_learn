package com.example.helloworld.share

import android.app.Activity
import android.content.Intent
import android.net.Uri

object ShareUtils {

    /**
     * @param text share content
     * @param panelTitle share panel's title
     */
    fun shareText(activity: Activity, text: String, panelTitle:String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, panelTitle)
        activity.startActivity(shareIntent)
    }

    fun shareBinaryContent(activity: Activity, mimeType:String, contentUri:Uri,panelTitle:String ) {
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, contentUri)
            type = mimeType
        }
        activity.startActivity(Intent.createChooser(shareIntent, panelTitle))
    }
}
package com.example.helloworld.share

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.text.buildSpannedString
import com.example.helloworld.application.AppContext


object ShareUtils {

    const val PACKAGE_MOBILE_QQ = "com.tencent.mobileqq"
    const val ACTIVITY_MOBILE_QQ = "com.tencent.mobileqq.activity.JumpActivity"


    /**
     * @param text share content
     * @param panelTitle share panel's title
     */
    fun shareText(activity: Activity, text: String, panelTitle: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, panelTitle)
        activity.startActivity(shareIntent)
    }

    fun shareBinaryContent(
        activity: Activity,
        mimeType: String,
        contentUri: Uri,
        panelTitle: String
    ) {
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, contentUri)
            type = mimeType
        }
        activity.startActivity(Intent.createChooser(shareIntent, panelTitle))
    }

    // 判断是否安装指定app
    fun isAppInstalled(packageName: String?): Boolean {
        return try {
            AppContext.getAppContext().packageManager.getPackageInfo(packageName!!, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 直接发送纯文本内容至QQ好友
     * @param context
     * @param content
     */
    fun sendTextToQQ(context: Context, content: String?) {
        if (isAppInstalled(PACKAGE_MOBILE_QQ)) {
            val intent = Intent("android.intent.action.SEND")
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, "分享")
            intent.putExtra(Intent.EXTRA_TEXT, content)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.component =
                ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity")
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "您需要安装QQ客户端", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * 分享图片给QQ好友
     *
     * @param bitmap
     */
    fun shareImageToQQ(context: Context, bitmap: Bitmap?) {
        if (isAppInstalled(PACKAGE_MOBILE_QQ)) {
            try {
                val uriToImage = Uri.parse(
                    MediaStore.Images.Media.insertImage(
                        context.contentResolver, bitmap, null, null
                    )
                )
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage)
                shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                shareIntent.type = "image/*"
                // 遍历所有支持发送图片的应用。找到需要的应用
                val componentName = ComponentName(
                    "com.tencent.mobileqq",
                    "com.tencent.mobileqq.activity.JumpActivity"
                )
                shareIntent.component = componentName
                context.startActivity(Intent.createChooser(shareIntent, "Share"))
            } catch (e: Exception) {

            }
        }
    }

    fun sendVideoToQQ(context: Context, uri: Uri) {
        if (isAppInstalled(PACKAGE_MOBILE_QQ)) {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "EXTRA_SUBJECT")
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            shareIntent.type = "video/mp4"
            // 遍历所有支持发送图片的应用。找到需要的应用
            val componentName = ComponentName(
                "com.tencent.mobileqq",
                "com.tencent.mobileqq.activity.JumpActivity"
            )
            shareIntent.component = componentName
            context.startActivity(Intent.createChooser(shareIntent, "分享"))
        } else {
            Toast.makeText(context, "您需要安装QQ客户端", Toast.LENGTH_LONG).show()
        }
    }

    fun sendFileToQQ(context: Context, uri: Uri, type: String) {
        if (isAppInstalled(PACKAGE_MOBILE_QQ)) {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "EXTRA_SUBJECT")
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            shareIntent.type = type
            // 遍历所有支持发送图片的应用。找到需要的应用
            val componentName = ComponentName(
                "com.tencent.mobileqq",
                "com.tencent.mobileqq.activity.JumpActivity"
            )
            shareIntent.component = componentName
            context.startActivity(Intent.createChooser(shareIntent, "分享"))
        } else {
            Toast.makeText(context, "您需要安装QQ客户端", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * @param context
     * @param uri uri generates from FileProvider
     * @param type Mime type
     * @param dstPkg destination app package name
     * @param dstCls destination app Activity class
     * @param panelTitle share panel's title (if has panel)
     */
    fun sendFileToApp(
        context: Context,
        uri: Uri,
        type: String,
        dstPkg: String,
        dstCls: String,
        panelTitle: String
    ) {
        if (isAppInstalled(dstPkg)) {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "EXTRA_SUBJECT")
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            shareIntent.type = type
            // 遍历所有支持发送图片的应用。找到需要的应用
            val componentName = ComponentName(
                dstPkg,
                dstCls
            )
            shareIntent.component = componentName
            context.startActivity(Intent.createChooser(shareIntent, panelTitle))
        } else {
            Toast.makeText(context, "您未安装该应用", Toast.LENGTH_LONG).show()
        }
    }
}
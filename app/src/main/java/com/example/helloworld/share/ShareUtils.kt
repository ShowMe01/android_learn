package com.example.helloworld.share

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.helloworld.application.AppContext
import com.example.helloworld.share.UriUtils.*
import java.io.File


object ShareUtils {

    const val PACKAGE_WECHAT = "com.tencent.mm"
    const val PACKAGE_MOBILE_QQ = "com.tencent.mobileqq"
    const val PACKAGE_SINA_WEIBO = "com.sina.weibo"
    const val PACKAGE_KUAISHOU = "com.smile.gifmaker"
    const val PACKAGE_DOUYIN = "com.ss.android.ugc.aweme" //抖音

    const val ACTIVITY_SHARE_WECHAT_SESSION = "com.tencent.mm.ui.tools.ShareImgUI" // 微信好友
    const val ACTIVITY_SHARE_WECHAT_FRIEND_CIRCLE =
        "com.tencent.mm.ui.tools.ShareToTimeLineUI" // 朋友圈 只能分析图片
    const val ACTIVITY_SHARE_QQ_FRIEND = "com.tencent.mobileqq.activity.JumpActivity" // QQ 分为图片和纯文本
    const val ACTIVITY_SHARE_SINA_WEIBO_FRIEND =
        "com.sina.weibo.weiyou.share.WeiyouShareDispatcher" // 微博好友
    const val ACTIVITY_SHARE_SINA_WEIBO_CONTENT =
        "com.sina.weibo.composerinde.ComposerDispatchActivity" // 微博内容
    const val ACTIVITY_SHARE_KUAISHOU = "com.yxcorp.gifshow.activity.UriRouterActivity" //快手
    const val ACTIVITY_SHARE_DOUYIN = "com.ss.android.ugc.aweme.share.SystemShareActivity" //抖音

    const val MIME_TYPE_IMG_JPEG = "image/jpg"
    const val MIME_TYPE_IMG_PNG = "image/png"
    const val MIME_TYPE_VIDEO_MP4 = "video/mp4"

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
        dstPkg: String,
        dstCls: String,
        panelTitle: String
    ) {
        if (isAppInstalled(dstPkg)) {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                this.type = context.contentResolver.getType(uri)
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TITLE, panelTitle)
                //传入的context不为Activity类型时，需要设置FLAG_ACTIVITY_NEW_TASK
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                //uri来自私有目录时，需要授予权限
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }

            //设置目标app，不设置则会弹出系统分享弹窗
            val componentName = ComponentName(
                dstPkg,
                dstCls
            )
            shareIntent.component = componentName
            context.startActivity(Intent.createChooser(shareIntent, panelTitle))
//            context.startActivity(shareIntent)
        } else {
            Toast.makeText(context, "没安装", Toast.LENGTH_LONG).show()
        }
    }

    fun getFileUri(context: Context, file: File): Uri? {
        var uri: Uri? = null
        // 低版本直接用 Uri.fromFile
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            uri = Uri.fromFile(file);
        } else {
            // 使用 FileProvider 会在某些 app 下不支持（在使用FileProvider 方式情况下QQ不能支持图片、视频分享，微信不支持视频分享）
            uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val cR = context.contentResolver
            uri?.let {
                val fileType = cR.getType(it)
                // 使用 MediaStore 的 content:// 而不是自己 FileProvider 提供的uri，不然有些app无法适配
                if (!TextUtils.isEmpty(fileType)) {
                    if (fileType?.contains("video/") == true) {
                        uri = getVideoContentUri(context, file);
                    } else if (fileType?.contains("image/") == true) {
                        uri = getImageContentUri(context, file);
                    } else if (fileType?.contains("audio/") == true) {
                        uri = getAudioContentUri(context, file);
                    }
                }
            }
        }
        return uri
    }

}
package com.example.helloworld.share

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.helloworld.R
import com.example.helloworld.application.AppContext
import com.example.helloworld.databinding.ActivityShareBinding
import com.permissionx.guolindev.PermissionX
import java.io.*


class TestShareActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ShareActivity"
    }

    private lateinit var binding: ActivityShareBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sendText.setOnClickListener {
            ShareUtils.shareText(this, "HelloWorld!", "Share")
        }

        binding.sendBitmap.setOnClickListener {
            val srcFile = File(filesDir, "images/feed_1.jpg")
            val uri: Uri? = FileProvider.getUriForFile(this, "${packageName}.fileprovider", srcFile)
            uri?.let {
                ShareUtils.sendFileToApp(
                    this,
                    uri,
                    PlatformUtil.PACKAGE_SINA,
                    PlatformUtil.ACTIVITY_SHARE_SINA_CONTENT, "分享"
                )
            }
        }

        binding.sendVideoWithSystem.setOnClickListener {
            val srcFile = File(filesDir, "videos/who_die_first_1.mp4")
            val dstFile = File(getExternalFilesDir(Environment.DIRECTORY_MOVIES), "who_die_first_2.mp4")
            Log.d(TAG, "onCreate: dstFile:${dstFile.absolutePath}")
            FileUtil.copyFile(srcFile,dstFile)
            val uri: Uri? = FileProvider.getUriForFile(this, "${packageName}.fileprovider", dstFile)
            ShareUtils.sendFileToApp(
                this,
                uri!!,
                ShareUtils.PACKAGE_SINA_WEIBO,
                ShareUtils.ACTIVITY_SHARE_SINA_WEIBO_CONTENT,
                "分享"
            )
            Log.d(TAG, "onCreate: uri:${uri}")
            /*val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
//            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "EXTRA_SUBJECT")
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.putExtra(Intent.EXTRA_TEXT, "Test Text String !!")
            shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            shareIntent.type = "video/mp4"
            startActivity(Intent.createChooser(shareIntent, "系统分享"))*/
        }

        binding.sendVideo.setOnClickListener {
            val srcFile = File(filesDir, "videos/who_die_first.mp4")
            val uri: Uri? = FileProvider.getUriForFile(this, "${packageName}.fileprovider", srcFile)
//            val uri = ShareUtils.getFileUri(this, srcFile)

            Log.d(TAG, "onCreate: uri:${uri}")
            uri?.let {
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "EXTRA_SUBJECT")
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                shareIntent.type = ShareUtils.MIME_TYPE_VIDEO_MP4

                // 授予目录临时共享权限
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

                //设置目标app
                val componentName = ComponentName(
                    PlatformUtil.PACKAGE_SINA,
                    PlatformUtil.ACTIVITY_SHARE_SINA_CONTENT,
                )
                shareIntent.component = componentName

//                startActivity(Intent.createChooser(shareIntent, "分享到新浪"))
                startActivity(shareIntent)
            }
        }

        binding.sendTextToReceiveActivity.setOnClickListener {
            startActivity(Intent().apply {
                action = Intent.ACTION_SEND
//                action = ReceiveActivity.MY_ACTION_SEND
                addCategory(Intent.CATEGORY_DEFAULT)
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "通过Intent.EXTRA_TEXT发送文本")
            })
        }

        binding.sendImgToReceiveActivity.setOnClickListener {
            startActivity(Intent().apply {
                action = Intent.ACTION_SEND
//                action = ReceiveActivity.MY_ACTION_SEND
                addCategory(Intent.CATEGORY_DEFAULT)
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, Uri.parse("https://alifei04.cfp.cn/creative/vcg/800/new/VCG211be3c9c31.jpg"))
            })
        }

        binding.sendVideoToReceiveActivity.setOnClickListener {
            startActivity(Intent().apply {
                action = Intent.ACTION_SEND
//                action = ReceiveActivity.MY_ACTION_SEND
                addCategory(Intent.CATEGORY_DEFAULT)
                type = "video/*"
                putExtra(Intent.EXTRA_STREAM, Uri.parse("https://www.w3schools.com/html/movie.mp4"))
            })
        }
    }

    private fun saveVideoToAlbum() {
        //1.将私有文件保存到相册
        val srcFile = File(filesDir, "videos/看谁先倒.mp4")
        val destFile = File(filesDir, "videos/看谁先倒1.mp4")
        //快手不支持中文名
        FileUtil.copyFile(srcFile, destFile)
//        val destFile = File(
//            AlbumNotifyUtils.getRootDirPath(),
//            System.currentTimeMillis().toString() + ".mp4"
//        )
//        FileUtil.copyFile(File(srcFile.toString()), destFile)

        AlbumNotifyUtils.insertVideoToMedia(System.currentTimeMillis(), destFile)
        val uri: Uri? = FileProvider.getUriForFile(this, "${packageName}.fileprovider", destFile)
        Log.d(TAG, "saveVideoToAlbum: uri:${uri?.toString()}")

        //2.将该视频相册uri 用于分享
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        shareIntent.type = "video/mp4"
        // 遍历所有支持发送图片的应用。找到需要的应用
        startActivity(Intent.createChooser(shareIntent, "分享视频"))

        //2.分享相册文件到其他应用

        /*ShareUtils.sendFileToApp(
            this,
            uri,
            "video/mp4",
            ShareUtils.PACKAGE_MOBILE_QQ,
            ShareUtils.ACTIVITY_MOBILE_QQ,
            "分享哈哈"
        )*/

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
    }

    private fun checksPermission(): Boolean {
        return PermissionX.isGranted(
            AppContext.getAppContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
}
package com.example.helloworld.qrcode

import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import com.example.helloworld.R
import com.example.helloworld.util.UIUtils
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlin.concurrent.thread

class QRCodeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode)

        thread {
            // 生成二维码
            val writer = QRCodeWriter()
            val size = UIUtils.getPixels(100f)
            val bitMatrix =
                writer.encode("https://www.baidu.com/", BarcodeFormat.QR_CODE, size, size)
            val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
            for (x in 0 until size) {
                for (y in 0 until size) {
                    bmp.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
                }
            }
            Handler(Looper.getMainLooper()).post {
                findViewById<ImageView>(R.id.ivQRCode).setImageBitmap(bmp)
            }
        }
    }
}
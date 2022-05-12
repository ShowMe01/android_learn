package com.example.helloworld.web

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.helloworld.R


class WebActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "WebActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        initWebView()
    }

    private fun initWebView() {
        val webView = findViewById<WebView>(R.id.webview).apply {
            setBackgroundColor(Color.TRANSPARENT)
        }

        val webSettings = webView.getSettings();

        val url =
            "https://test-m.modd.vip/fep/wxtietie/setProblem.html?_bid=1002593&_wk=1&_resize=1&_source=feed&feedid=44965237&newPaper=0&SESSIONID=D3211844-DB96-4449-8E40-99660EDE04BD&source=more_tab"
        val baiduUrl = "https://www.baidu.com"
        webView.loadUrl(baiduUrl)

        //设置WebChromeClient类
        webView.setWebChromeClient(object : WebChromeClient() {
            //获取网站标题
            override fun onReceivedTitle(view: WebView?, title: String?) {
                Log.d(TAG, "onReceivedTitle: ")
            }

            //获取加载进度
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                Log.d(TAG, "onProgressChanged: progress:${newProgress}")
            }
        })

        //设置WebViewClient类
        webView.setWebViewClient(object : WebViewClient() {
            //设置加载前的函数
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                Log.d(TAG, "onPageStarted: 开始加载了")
            }

            //设置结束加载函数
            override fun onPageFinished(view: WebView?, url: String?) {
                Log.d(TAG, "onPageFinished: 加载结束了")
            }
        })
    }
}
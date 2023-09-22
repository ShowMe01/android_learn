package com.example.helloworld.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import com.example.helloworld.R
import com.google.gson.Gson
import com.tencent.mmkv.MMKV

class Widget4_2ConfigActivity : AppCompatActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private var content: String = ""

    companion object {
        private const val TAG = "WidgetConfigActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_config_4_2)

        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            handleCancel()
            return
        }

        findViewById<View>(R.id.btnWoodenFish).setOnClickListener {
            //todo
            MMKV.defaultMMKV().putString("widget_${appWidgetId}", "")
        }

    }


    private fun handleOk() {
        val widgetConfiguration = MMKV.defaultMMKV().getString("widget_configure", "")
        val widgetList =
            Gson().fromJson(widgetConfiguration, WidgetList::class.java) ?: WidgetList()

        val element = WidgetModel(appWidgetId, content)
        widgetList.widgetList.add(element)

        //1.保存widgetId和内容
        val listJson = Gson().toJson(widgetList)
        Log.d(TAG, "handleOk: listJson : $listJson")
        MMKV.defaultMMKV().putString("widget_configure", listJson)

        //2.更新widget
        val views = RemoteViews(packageName, R.layout.config_widget)
        views.setTextViewText(R.id.appwidget_text, content)
        AppWidgetManager.getInstance(this@Widget4_2ConfigActivity)
            .updateAppWidget(appWidgetId, views)

        //3.传递结果
        val intent = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //用户取消创建
        setResult(Activity.RESULT_CANCELED)
    }

    private fun handleCancel() {
        finish()
        setResult(Activity.RESULT_CANCELED)
    }
}
package com.example.helloworld.widget

import android.appwidget.AppWidgetManager

data class WidgetModel(
    var id:Int = AppWidgetManager.INVALID_APPWIDGET_ID,
    var content:String = "",
)

data class WidgetList(
    val widgetList:MutableList<WidgetModel> = mutableListOf()
)
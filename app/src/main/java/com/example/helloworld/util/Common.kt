package com.example.helloworld.util

import android.content.res.Resources

// 扩展属性 for Int
val Int.dp: Float
    get() = (this / Resources.getSystem().displayMetrics.density)


// 扩展属性 for Float
val Float.dp: Float
    get() = (this / Resources.getSystem().displayMetrics.density)

// 扩展属性 for Int to px
val Int.px: Float
    get() = (this * Resources.getSystem().displayMetrics.density)

// 扩展属性 for Float to px
val Float.px: Float
    get() = (this * Resources.getSystem().displayMetrics.density)
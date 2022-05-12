package com.example.helloworld.ime

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.helloworld.databinding.ActivityImeBinding

class ImeActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityImeBinding

    private val TAG = "ImeActivity"
    private var lastWindowHeight = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityImeBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.btnOpen.setOnClickListener {
            SoftInputUtil.showSoftInput(it)
        }

        viewBinding.btnClose.setOnClickListener {
            SoftInputUtil.hideSoftInput(it)
        }

        viewBinding.btnToast.setOnClickListener {
            Toast.makeText(this, "展示toast", Toast.LENGTH_LONG).show()
        }

        viewBinding.btnAddWindow.setOnClickListener {
            val lp = WindowManager.LayoutParams()
            lp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            lp.alpha = 0.3f
            lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            val view = View(this)
            windowManager.addView(view, lp)

            view.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                val rootViewRect = Rect()
                view.getWindowVisibleDisplayFrame(rootViewRect)
                Log.d(TAG, "btnAddWindow addOnLayoutChangeListener: $top , $bottom , $oldTop, $oldBottom")
                Log.d(TAG, "btnAddWindow addOnLayoutChangeListener: rect: $rootViewRect")
            }

            view.viewTreeObserver.addOnGlobalLayoutListener {
                val rootViewRect = Rect()
                view.getWindowVisibleDisplayFrame(rootViewRect)
                Log.d(TAG, "btnAddWindow: addOnGlobalLayoutListener rect: $rootViewRect")
            }
        }

        viewBinding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val rootViewRect = Rect()
            viewBinding.root.getWindowVisibleDisplayFrame(rootViewRect)
            val curWindowHeight = rootViewRect.height()
            if (curWindowHeight > lastWindowHeight || lastWindowHeight == 0) {
                // 初始化高度
                lastWindowHeight = curWindowHeight
            } else if (curWindowHeight < lastWindowHeight) {
                //输入法打开
                viewBinding.rlEditCommentContainer.visibility = View.VISIBLE
                viewBinding.rlEditCommentContainer.translationY =
                    curWindowHeight - lastWindowHeight.toFloat()
            } else {
                //输入法关闭
                viewBinding.rlEditCommentContainer.visibility = View.INVISIBLE
            }
            Log.d(TAG, "onCreate: rootView rect: $rootViewRect")
        }

        /*SoftInputUtil().attachSoftInput(
            viewBinding.rlEditCommentContainer
        ) { isSoftInputShow, softInputHeight, viewOffset ->
            if (isSoftInputShow) {
                viewBinding.rlEditCommentContainer.visibility = View.VISIBLE
                viewBinding.rlEditCommentContainer.translationY =
                    viewBinding.rlEditCommentContainer.translationY + viewOffset
            } else {
                viewBinding.rlEditCommentContainer.visibility = View.INVISIBLE
            }
        }*/
    }
}
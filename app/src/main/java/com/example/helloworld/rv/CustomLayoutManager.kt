package com.example.helloworld.rv

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class CustomLayoutManager : RecyclerView.LayoutManager() {

    private val TAG = "CustomLayoutManager"

    /**
     * RecyclerView总偏移量
     */
    private var mScrollY = 0

    /**
     * 记录所有item总高度。
     * 在item数过少未充满RecyclerView时，取RecyclerView高度
     */
    private var mTotalHeight = 0

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        Log.d(TAG, "generateDefaultLayoutParams: ")
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        Log.d(TAG, "onLayoutChildren: state:${state}")
        super.onLayoutChildren(recycler, state)
        //1.添加所有的item view 并进行测量布局
        var offsetY = 0
        for (i in 0 until itemCount) {
            val view = recycler.getViewForPosition(i)
            addView(view)
            measureChildWithMargins(view, 0, 0)
            val itemWidth = getDecoratedMeasuredWidth(view)
            val itemHeight = getDecoratedMeasuredHeight(view)
            layoutDecoratedWithMargins(view, 0, offsetY, itemWidth, offsetY + itemHeight)
            offsetY += itemHeight
        }

        mTotalHeight = Math.max(offsetY, getVerticalSpace())
    }


    override fun canScrollVertically(): Boolean {
        return true
    }

    /**
     * 由下向上滑时，item position 应增加，dy>0
     */
    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        Log.d(TAG, "scrollVerticallyBy: dy:${dy}")
        //1.限制边界滑动
        var travel = dy
        if (mScrollY + dy < 0) {
            travel = -mScrollY
        } else if (mScrollY + dy > mTotalHeight - getVerticalSpace()) {
            travel = mTotalHeight - getVerticalSpace() - mScrollY
        }

        mScrollY += travel
        offsetChildrenVertical(-travel)
        return dy
    }

    fun getVerticalSpace(): Int {
        val visibleHeight = height - paddingTop - paddingBottom
        Log.d(TAG, "getVerticalSpace: height:${height} ，paddingTop:${paddingTop}, paddingBottom:${paddingBottom}")
        return visibleHeight
    }
}
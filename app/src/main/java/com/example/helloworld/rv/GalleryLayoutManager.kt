package com.example.helloworld.rv

import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * 横向画廊
 */
class GalleryLayoutManager : RecyclerView.LayoutManager() {

    private val TAG = "CustomLayoutManager"

    /**
     *  简单处理，因为每个item宽高一致，且布局规则相同，所以可以动态计算
     */
    private var mItemRect = Rect()

    /**
     * 获取当前RecyclerView可见区域
     */
    private var mRvRect = Rect()

    /**
     * RecyclerView总偏移量
     */
    private var mScrollX = 0

    private var mTotalWidth = 0

    private var mItemWidth = 0
    private var mItemHeight = 0

    private var mIntervalWidth = 0

    /**
     * 初始化时首个item居中
     */
    private var mStartX = 0

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        Log.d(TAG, "generateDefaultLayoutParams: ")
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        Log.d(TAG, "onLayoutChildren: state:${state}")
        detachAndScrapAttachedViews(recycler)
        if (itemCount == 0) {
            return
        }

        //0.每个item都相同的情况下，可以预先计算item宽高、每个item位置
        val itemView = recycler.getViewForPosition(0)
        measureChildWithMargins(itemView, 0, 0)
        mItemWidth = getDecoratedMeasuredWidth(itemView)
        mItemHeight = getDecoratedMeasuredHeight(itemView)
        mIntervalWidth = getIntervalWidth()

        mStartX = width / 2 - mIntervalWidth

        //初始化时，前面的visible pos 和 item pos 一致
        val visibleCount = Math.ceil((getHorizontalSpace() * 1.0 / mIntervalWidth)).toInt()
        val visibleArea = getVisibleArea()
        repeat(visibleCount) { pos ->
            insertView(pos, visibleArea, recycler, false)
        }

        mTotalWidth = Math.max(getItemInitialLayout(itemCount - 1).right, getHorizontalSpace())
    }

    override fun canScrollHorizontally(): Boolean {
        return true
    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        if (itemCount <= 0) {
            return dx
        }

        //1.限制边界滑动
        var travel = dx
        if (mScrollX + dx < 0) {
            travel = -mScrollX
        } else if (mScrollX + dx > mTotalWidth - getHorizontalSpace()) {
            travel = mTotalWidth - getHorizontalSpace() - mScrollX
        }

        mScrollX += travel
        val visibleArea = getVisibleArea()

        //2.遍历所有当前可见HolderView，回收即将不可见的、重新布局一直可见的
        Log.d(TAG, "scrollHorizontallyBy: beforeRemove childCount:${childCount}")
        for (i in 0 until childCount) {
            val view = getChildAt(i) ?: continue
            val adapterPos = getPosition(view)
            val childRect = getItemInitialLayout(adapterPos)
            if (!Rect.intersects(childRect, visibleArea)) {
                removeAndRecycleView(view, recycler)
            } else {
                layoutDecoratedWithMargins(
                    view,
                    childRect.left - mScrollX,
                    childRect.top,
                    childRect.right - mScrollX,
                    childRect.bottom
                )
                handleViewTransform(view, childRect.left - mStartX - mScrollX)
            }
        }

        Log.d(TAG, "scrollVerticallyBy: afterRemove childCount:${childCount}")
        //3.1 根据滑动，添加即将出现的view
        val firstVisibleView = getChildAt(0) ?: return travel
        val lastVisibleView = getChildAt(childCount - 1) ?: return travel
        if (travel > 0) {
            val minPos = getPosition(lastVisibleView) + 1
            for (adapterPos in minPos until itemCount) {
                val isSuc = insertView(adapterPos, visibleArea, recycler, false)
                if (!isSuc) break
            }
        } else {
            val maxPos = getPosition(firstVisibleView) - 1
            for (adapterPos in maxPos downTo 0) {
                val isSuc = insertView(adapterPos, visibleArea, recycler, true)
                if (!isSuc) break
            }
        }

        return travel
    }

    /**
     * @return true 代表此次插入成功
     */
    private fun insertView(
        adapterPos: Int,
        visibleArea: Rect,
        recycler: RecyclerView.Recycler,
        addToFirst: Boolean
    ): Boolean {
        val itemRect = getItemInitialLayout(adapterPos)
        if (Rect.intersects(itemRect, visibleArea)) {
            val itemView = recycler.getViewForPosition(adapterPos)
            if (addToFirst) {
                addView(itemView, 0)
            } else {
                addView(itemView)
            }
            measureChildWithMargins(itemView, 0, 0)
            layoutDecoratedWithMargins(
                itemView,
                itemRect.left - mScrollX,
                itemRect.top,
                itemRect.right - mScrollX,
                itemRect.bottom
            )
            handleViewTransform(itemView, itemRect.left - mStartX - mScrollX)
            return true
        } else {
            return false
        }
    }

    private fun getHorizontalSpace(): Int {
        return width - paddingLeft - paddingRight
    }

    private fun getVerticalSpace(): Int {
        val visibleHeight = height - paddingTop - paddingBottom
        /*Log.d(
            TAG,
            "getVerticalSpace: height:${height} ，paddingTop:${paddingTop}, paddingBottom:${paddingBottom}"
        )*/
        return visibleHeight
    }

    private fun getItemInitialLayout(position: Int): Rect {
        mItemRect.set(
            mStartX + position * mIntervalWidth,
            0,
            mStartX + position * mIntervalWidth + mItemWidth,
            mItemHeight
        )
        return mItemRect
    }

    /**
     * 获取可见区域，以第一个item左上角为原点
     */
    private fun getVisibleArea(): Rect {
        mRvRect.set(
            paddingLeft + mScrollX,
            paddingTop,
            width - paddingRight + mScrollX,
            getVerticalSpace()
        )
        return mRvRect
    }

    private fun getIntervalWidth(): Int {
        return mItemWidth / 2
    }

    fun getCenterPosition(): Int {
        var pos = mScrollX / getIntervalWidth()
        val more = mScrollX % getIntervalWidth()
        if (more > getIntervalWidth() * 0.5) {
            pos++
        }
        return pos
    }

    fun getFirstPosition(): Int {
        if (childCount < 0) {
            return 0
        }
        val firstVisibleView = getChildAt(0) ?: return 0
        return getPosition(firstVisibleView)
    }

    /**
     * 在此方法中处理图形变换
     */
    private fun handleViewTransform(child: View, moveX: Int) {
        val radio = computeScale(moveX)
        child.scaleX = radio
        child.scaleY = radio
    }

    private fun computeScale(x: Int): Float {
        var scale = 1 - Math.abs(x * 1.0f / (8f * getIntervalWidth()))
        if (scale < 0) scale = 0f
        if (scale > 1) scale = 1f
        return scale
    }
}

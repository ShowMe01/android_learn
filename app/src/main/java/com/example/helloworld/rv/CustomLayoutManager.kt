package com.example.helloworld.rv

import android.graphics.Rect
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class CustomLayoutManager : RecyclerView.LayoutManager() {

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
    private var mScrollY = 0

    /**
     * 记录所有item总高度。
     * 在item数过少未充满RecyclerView时，取RecyclerView高度
     */
    private var mTotalHeight = 0

    private var mItemWidth = 0
    private var mItemHeight = 0

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        Log.d(TAG, "generateDefaultLayoutParams: ")
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    /**
     * 初始化ViewHolder布局
     * 简单复用：
     *  1.使用detachAndScrapAttachedViews(recycler)将所有可见view剥离
     *  2.只创建可见数量的HolderView，撑满初始化的一屏即可
     *
     */
    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        Log.d(TAG, "onLayoutChildren: state:${state}")
        super.onLayoutChildren(recycler, state)

        detachAndScrapAttachedViews(recycler)
        if (itemCount == 0) {
            return
        }

        //0.每个item都相同的情况下，可以预先计算item宽高、每个item位置
        val itemView = recycler.getViewForPosition(0)
        measureChildWithMargins(itemView, 0, 0)
        mItemWidth = getDecoratedMeasuredWidth(itemView)
        mItemHeight = getDecoratedMeasuredHeight(itemView)

        //初始化时，前面的visible pos 和 item pos 一致
        val visibleCount = getVerticalSpace() / mItemHeight + 1
        repeat(visibleCount) { pos ->
            val visibleView = recycler.getViewForPosition(pos)
            addView(visibleView)
            measureChildWithMargins(visibleView, 0, 0)
            val layoutRect = getItemInitialLayout(pos)
            layoutDecorated(
                visibleView,
                layoutRect.left,
                layoutRect.top,
                layoutRect.right,
                layoutRect.bottom
            )
        }

        mTotalHeight = Math.max(getItemInitialLayout(itemCount - 1).bottom, getVerticalSpace())
    }


    override fun canScrollVertically(): Boolean {
        return true
    }

    /**
     * 由下向上滑时，item position 应增加，dy>0
     * 简单复用：
     *  1.判断滑动后哪些HolderView需要回收，对其调用removeAndRecycleView(child, recycler)
     *  2.使用 recycler.getViewForPosition(position) 从缓存中获取HolderView填充空白区域
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

        //2.遍历所有当前可见HolderView，移除即将不可见的
        for (i in childCount-1 downTo 0) {
            val view = getChildAt(i) ?: continue
            if (travel > 0) {
                if (getDecoratedBottom(view) - travel < 0) {
                    removeAndRecycleView(view, recycler)
                    continue
                }
            } else {
                if (getDecoratedTop(view) - travel > getVerticalSpace()) {
                    removeAndRecycleView(view, recycler)
                    continue
                }
            }
        }

        //3.添加即将可见的
        val screenRect = getVisibleArea(travel)
        if (travel > 0) {
            val lastView = getChildAt(childCount - 1) ?: return dy
            //minPos第一个即将可见的位置
            val minPos = getPosition(lastView) + 1
            for (i in minPos until itemCount) {
                val rect = getItemInitialLayout(i)
                if (Rect.intersects(rect, screenRect)) {
                    val view = recycler.getViewForPosition(i)
                    addView(view)
                    measureChildWithMargins(view, 0, 0)
                    //这里view的位置，都是以RecyclerView左上角为基准
                    layoutDecorated(
                        view,
                        rect.left,
                        rect.top - mScrollY,
                        rect.right,
                        rect.bottom - mScrollY
                    )
                } else {
                    break
                }
            }
        } else {
            val firstVisibleView = getChildAt(0) ?: return dy
            val maxAdapterPos = getPosition(firstVisibleView) - 1
            for (i in maxAdapterPos downTo 0) {
                val itemRect = getItemInitialLayout(i)
                if (Rect.intersects(itemRect, screenRect)) {
                    val view = recycler.getViewForPosition(i)
                    //下滑时，顶部需要填充view，所以要往最前面加
                    addView(view, 0)
                    measureChildWithMargins(view, 0, 0)
                    layoutDecorated(
                        view,
                        itemRect.left,
                        itemRect.top - mScrollY,
                        itemRect.right,
                        itemRect.bottom - mScrollY
                    )
                } else {
                    break
                }
            }
        }

        mScrollY += travel
        offsetChildrenVertical(-travel)
        return travel
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
        mItemRect.set(0, position * mItemHeight, mItemWidth, (position + 1) * mItemHeight)
        return mItemRect
    }

    private fun getVisibleArea(travel: Int): Rect {
        mRvRect.set(
            paddingLeft,
            paddingTop + mScrollY + travel,
            width - paddingRight,
            getVerticalSpace() + mScrollY + travel
        )
        return mRvRect
    }
}
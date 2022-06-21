package com.example.helloworld.rv

import android.graphics.Rect
import android.util.Log
import android.util.SparseBooleanArray
import android.view.ViewGroup
import androidx.core.util.set
import androidx.recyclerview.widget.RecyclerView

class CustomLayoutManager : RecyclerView.LayoutManager() {

    private val TAG = "CustomLayoutManager"

    private val mHasAttachedItems = HashMap<Int, Boolean>()

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
        mHasAttachedItems.clear()
//        repeat(itemCount) { pos->
//            mHasAttachedItems.put(pos, false)
//        }

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
     * 复杂复用：
     *  1.回收view部分相同，都是将即将不可见的view移除。
     *  2.添加view部分有修改
     *      2.1 不再使用offsetChildrenVertical处理滑动，而是先将所有view都detach移除，
     *      2.2 重新判断所有view可见性，并按移动后的位置添加。
     *      这样的好处是，可以将简单的滑动替换为各种复杂的图形变换，如穿插缩放、旋转等
     *  3.继续优化回收
     *      3.1 回收时仅回收即将不可见view、可见的view则直接重新布局
     *      3.2 对于所有可见的view，判断其是否已添加，若已添加则重新布局，否则从回收池中获取再布局
     */
    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        Log.d(TAG, "scrollVerticallyBy: dy:${dy} , mapSize:${mHasAttachedItems.size}")
        if (itemCount <= 0) {
            return dy
        }

        //1.限制边界滑动
        var travel = dy
        if (mScrollY + dy < 0) {
            travel = -mScrollY
        } else if (mScrollY + dy > mTotalHeight - getVerticalSpace()) {
            travel = mTotalHeight - getVerticalSpace() - mScrollY
        }

        mScrollY += travel
        val visibleArea = getVisibleArea()

        //2.遍历所有当前可见HolderView，回收即将不可见的、重新布局一直可见的
        Log.d(TAG, "scrollVerticallyBy: beforeRemove childCount:${childCount}")
        for (i in 0 until childCount) {
            val view = getChildAt(i) ?: continue
            val adapterPos = getPosition(view)
            val childRect = getItemInitialLayout(adapterPos)
            if (!Rect.intersects(childRect, visibleArea)) {
                removeAndRecycleView(view, recycler)
                mHasAttachedItems.put(adapterPos, false)
            } else {
                layoutDecorated(
                    view,
                    childRect.left,
                    childRect.top - mScrollY,
                    childRect.right,
                    childRect.bottom - mScrollY
                )
                mHasAttachedItems.put(adapterPos, true)
            }
        }

        //3.统一重新判断view可见性
        Log.d(TAG, "scrollVerticallyBy: afterRemove childCount:${childCount}")
       /* if (travel > 0) {
             val minPos = getPosition(firstVisibleView)
             for (i in minPos until itemCount) {
                 insertView(i, visibleArea, recycler, false)
             }
         } else {
             val maxPos = getPosition(lastVisibleView)
             //永远注意 这里遍历的顺序和addView的顺序
             for (i in maxPos downTo 0) {
                 insertView(i, visibleArea, recycler, true)
             }
         }*/
        //3.1 根据滑动，添加即将出现的view
        val firstVisibleView = getChildAt(0) ?: return travel
        val lastVisibleView = getChildAt(childCount - 1) ?: return travel
        if (travel > 0) {
            val minPos = getPosition(lastVisibleView) + 1
            for (adapterPos in minPos until itemCount) {
               val isSuc = insertView(adapterPos,visibleArea,recycler,false)
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
        val hasAttached = mHasAttachedItems[adapterPos] ?: false
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
                itemRect.left,
                itemRect.top - mScrollY,
                itemRect.right,
                itemRect.bottom - mScrollY
            )
            itemView.rotationY = itemView.rotationY + 1
            mHasAttachedItems[adapterPos] = true
            return true
        } else {
            return false
        }
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

    /**
     * 获取可见区域，以第一个item左上角为原点
     */
    private fun getVisibleArea(): Rect {
        mRvRect.set(
            paddingLeft,
            paddingTop + mScrollY,
            width - paddingRight,
            getVerticalSpace() + mScrollY
        )
        return mRvRect
    }
}
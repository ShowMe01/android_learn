package com.example.helloworld.rv

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.Rect
import android.util.Log
import android.util.SparseBooleanArray
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.recyclerview.widget.RecyclerView

/**
 * 布局规则：所有item自底向上、靠右布局
 * TODO 复用问题
 */
class GearLayoutManager : RecyclerView.LayoutManager() {
    private val TAG = "GearLayoutManager"

    private var mVisibleArea = Rect()

    /**item 自底向上移动 */
    private val ITEM_FROM_BOTTOM_TO_TOP = 1

    /**item 自顶向下移动*/
    private val ITEM_FROM_TOP_TO_BOTTOM = 2

    /**
     * 当前滑动量
     */
    private var mScrollY = 0

    /**
     * 初始时，底部item向上的偏移量，保证首个item在列表中央
     */
    private var mStartY = 0

    /**RecyclerView的Item回收器 */
    private var mRecycler: RecyclerView.Recycler? = null

    /**RecyclerView的状态器 */
    private var mState: RecyclerView.State? = null

    /**正显示在中间的Item */
    private var mSelectPosition = 0

    /**前一个正显示在中间的Item */
    private var mLastSelectPosition = 0

    /**滚动动画 */
    private var mAnimation: ValueAnimator? = null

    /**记录Item是否出现过屏幕且还没有回收。true表示出现过屏幕上，并且还没被回收 */
    private val mHasAttachedItems = SparseBooleanArray()

    /**由中心向两边尝试检查是否需要绘制的item数*/
    private val MAX_CHECK_COUNT = 3

    /**边缘预加载item数量*/
    private val MARGIN_PRELOAD_COUNT = 1

    /**选中监听 */
    private var selectChangeListener: OnSelectedChangeListener? = null

    private var mItemWidth = 0
    private var mItemHeight = 0

    private val mItemTempRect = Rect()

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        //如果没有item，直接返回
        //跳过preLayout，preLayout主要用于支持动画
        if (itemCount <= 0 || state.isPreLayout) {
            mScrollY = 0
            return
        }
        mHasAttachedItems.clear()

        //计算item宽高，每个item都相同
        val itemView = recycler.getViewForPosition(0)

        measureChildWithMargins(itemView, 0, 0)
        mItemWidth = getDecoratedMeasuredWidth(itemView)
        mItemHeight = getDecoratedMeasuredHeight(itemView)

        mStartY = getVerticalSpace() / 2 - mItemHeight / 2

        detachAndScrapAttachedViews(recycler) //在布局之前，将所有的子View先Detach掉，放入到Scrap缓存中
        if ((mRecycler == null || mState == null) &&  //在初始化前调用smoothScrollToPosition 或者 scrollToPosition,只会记录位置
            mSelectPosition != 0
        ) {                 //所以初始化时需要滚动到对应位置
            mScrollY = calculateOffsetForPosition(mSelectPosition)
        }

        layoutItems(recycler, state, ITEM_FROM_BOTTOM_TO_TOP)

        mRecycler = recycler
        mState = state
    }

    private fun onSelectChangeCallBack(direction: Int) {
        val newPos = getCenterPosition()
        if (newPos != mSelectPosition) {
            mLastSelectPosition = mSelectPosition
            mSelectPosition = newPos
            selectChangeListener?.onItemChanged(mLastSelectPosition, mSelectPosition)
        }
    }

    /**
     * 记录到目标位置需要的偏移量
     */
    private fun calculateOffsetForPosition(position: Int): Int {
        return Math.round((position * getIntervalHeight()).toDouble()).toInt()
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        if (mAnimation != null && mAnimation?.isRunning == true) {
            mAnimation?.cancel()
        }

        if (itemCount <= 0) {
            return dy
        }

        //限制滑动位置
        var travel = dy
        if (mScrollY - dy < 0) {
            travel = -mScrollY
        } else if (mScrollY - dy > getVerticalMaxOffset()) {
            travel = mScrollY - getVerticalMaxOffset()
        }

        mScrollY -= travel //累加偏移量

        layoutItems(
            recycler,
            state,
            if (travel > 0) ITEM_FROM_BOTTOM_TO_TOP else ITEM_FROM_TOP_TO_BOTTOM
        )
        return travel
    }

    private fun getVerticalMaxOffset(): Int {
        return if (itemCount < 0) {
            0
        } else {
            (itemCount - 1) * getIntervalHeight()
        }
    }

    private fun getIntervalHeight(): Int {
        return (mItemHeight * 0.93f).toInt()
    }

    /**
     * 1.先清除超出屏幕的item
     * 2.绘制可见item
     */
    private fun layoutItems(
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State?,
        direction: Int,
    ) {
        if (state == null || state.isPreLayout) return
        val start = System.currentTimeMillis()
        val visibleArea = getVisibleArea()
        var position = 0
        val centerPosition = getCenterPosition()
        for (i in 0 until childCount) {
            val child = getChildAt(i) ?: continue
            position = if (child.tag != null && child.tag is Int) {
                child.tag as Int
            } else {
                getPosition(child)
            }
            val rect: Rect = getItemCurLayout(position)
            //Item没有在显示区域，就说明需要回收
            if (!Rect.intersects(visibleArea, rect)) {
                removeAndRecycleView(child, recycler) //回收滑出屏幕的View
                mHasAttachedItems.delete(position)
            } else { //Item还在显示区域内，更新滑动后Item的位置
                layoutItem(child, rect) //更新Item位置
                mHasAttachedItems.put(position, true)
            }
            Log.d(TAG, "layoutItems: mHasAttachedItems:${mHasAttachedItems}")
        }

        if (position == 0) {
            //当前所有view都未添加，需要从中间位置向两边添加view
            position = getCenterPosition()
        }

        val minPos = Math.max(position - MAX_CHECK_COUNT, 0)
        val maxPos = Math.min(position + MAX_CHECK_COUNT, itemCount - 1)
        //这里的遍历顺序和后面addView顺序有关系
        if (direction == ITEM_FROM_TOP_TO_BOTTOM) {
            for (i in minPos..maxPos) {
                insertView(i, visibleArea, recycler, direction)
            }
        } else {
            for (i in maxPos downTo minPos) {
                insertView(i, visibleArea, recycler, direction)
            }
        }
        Log.d(
            "testChange",
            "layoutItems: layout duration:${System.currentTimeMillis() - start} "
        )
        onSelectChangeCallBack(direction)

    }

    private fun insertView(
        pos: Int,
        visibleArea: Rect,
        recycler: RecyclerView.Recycler,
        direction: Int
    ) {
        val rect: Rect = getItemCurLayout(pos)
        val centerPos = getCenterPosition()
        //重新加载可见范围内、未被绘制的Item
        if (Rect.intersects(
                visibleArea,
                rect
            ) && !mHasAttachedItems.get(pos)
        ) {
            val scrap = recycler.getViewForPosition(pos)
            Log.d(TAG, "insertView: pos:${pos}")
            scrap.tag = pos

            if (direction == ITEM_FROM_BOTTOM_TO_TOP) { //item 向上滑动，底部空了出来需要填充新item
                addView(scrap, 0)
            } else { //item 向下滚动，顶部空了需要填充新view
                addView(scrap)
            }
            measureChildWithMargins(scrap, 0, 0)
            layoutItem(scrap, rect) //将这个Item布局出来
            mHasAttachedItems.put(pos, true)
        }
    }

    /**
     * 根据偏移量计算最接近当前中心的item下标
     */
    private fun getCenterPosition(): Int {
        var scrollN = (mScrollY * 1f / getIntervalHeight()).toInt()
        val more = mScrollY % getIntervalHeight()
        if (Math.abs(more) > getIntervalHeight() * 0.5) {
            scrollN++
        }
        return scrollN
    }

    private fun layoutItem(child: View, itemRect: Rect) {
        layoutDecoratedWithMargins(
            child,
            itemRect.left,
            itemRect.top,
            itemRect.right,
            itemRect.bottom
        )
        handleViewTransform(child, itemRect.centerY() - getVerticalSpace() / 2)
    }

    private fun handleViewTransform(itemView: View, moveY: Int) {
        //缩放
        val ratio = Math.abs(moveY * 1f / mItemHeight)
        val scale = TransformUtils.mix(1f, 0.6f, ratio)
        itemView.scaleX = scale
        itemView.scaleY = scale

        //旋转
        val rotateRatio = Math.abs(moveY * 1f / mItemHeight)
        var angle = TransformUtils.mix(0f, 10f, rotateRatio)
        if (moveY > 0) {
            angle = -angle
        }
        itemView.rotation = angle
        itemView.post {
            //平移
            val tRatio = Math.abs(moveY * 1f / mItemHeight)
            val translationX = TransformUtils.mix(0f, 230f / 700f * itemView.height, tRatio)

            itemView.translationX = translationX
        }
    }

    /**
     * 首个view对齐底部，后续view向上排列
     */
    private fun getItemInitialLayout(position: Int): Rect {
        return getItemLayout(position, 0)
    }

    private fun getItemLayout(position: Int, offsetY: Int): Rect {
        mItemTempRect.set(
            getHorizontalSpace() - mItemWidth,
            getVerticalSpace() - position * getIntervalHeight() - mItemHeight - mStartY + offsetY,
            getHorizontalSpace(),
            getVerticalSpace() - position * getIntervalHeight() - mStartY + offsetY
        )
        return mItemTempRect
    }

    private fun getItemCurLayout(position: Int): Rect {
        return getItemLayout(position, mScrollY)
    }

    /**
     * 根据当前偏移量计算可见view
     */
    fun isItemVisible(adapterPos: Int): Boolean {
        val visibleArea = getVisibleArea()
        val itemCurLayout = getItemCurLayout(adapterPos)
        return Rect.intersects(visibleArea, itemCurLayout)
    }

    fun isItemCompletelyVisible(adapterPos: Int): Boolean {
        val visibleArea = getVisibleArea()
        val itemCurLayout = getItemCurLayout(adapterPos)
        return visibleArea.contains(itemCurLayout)
    }

    private fun getVisibleArea(): Rect {
        mVisibleArea.set(
            paddingLeft,
            paddingTop,
            width - paddingRight,
            height - paddingBottom
        )
        return mVisibleArea
    }

    private fun getVerticalSpace(): Int {
        return height - paddingTop - paddingBottom
    }

    private fun getHorizontalSpace(): Int {
        return width - paddingLeft - paddingRight
    }

    override fun canScrollVertically(): Boolean {
        return true
    }


    fun calculateFlingY(velocityY: Int): Int {
        var flingY = (velocityY * 0.4f).toInt()
        val distance = FlingUtils.getSplineFlingDistance(flingY)
        val newDistance = this.calculateFlingDistance(velocityY, distance)
        val fixVelocityY = FlingUtils.getVelocity(newDistance)
        flingY = if (velocityY > 0) {
            fixVelocityY
        } else {
            -fixVelocityY
        }
        return flingY
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        when (state) {
            RecyclerView.SCROLL_STATE_IDLE -> {
                //滚动停止时
                fixOffsetWhenFinishScroll()
            }
        }
    }

    /**
     * 修正停止滚动后，Item滚动到中间位置
     */
    private fun fixOffsetWhenFinishScroll() {
        if (getIntervalHeight() > 0) {
            val targetPos = getCenterPosition()
            val finalOffset: Int = (targetPos * getIntervalHeight())
            startScroll(mScrollY, finalOffset)
        }
    }

    /**
     * 滚动到指定X轴位置
     * @param from X轴方向起始点的偏移量
     * @param to X轴方向终点的偏移量
     */
    private fun startScroll(from: Int, to: Int) {
        if (mAnimation?.isRunning == true) {
            mAnimation?.cancel()
        }
        val direction = if (from < to) ITEM_FROM_TOP_TO_BOTTOM else ITEM_FROM_BOTTOM_TO_TOP
        mAnimation = ValueAnimator.ofFloat(from.toFloat(), to.toFloat()).apply {
            duration = 300
            interpolator = AccelerateInterpolator()
            addUpdateListener {
                if (it.animatedValue is Float) {
                    mScrollY = Math.round(it.animatedValue as Float)
                    val finalRecycler = mRecycler
                    val finalState = mState
                    if (finalRecycler != null && finalState != null) {
                        layoutItems(finalRecycler, finalState, direction)
                    }
                }
            }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationRepeat(animation: Animator?) {
                }
            })
        }
        mAnimation?.start()

    }

    /**
     * @param distance 本次实际滑动距离
     * @return 调整后的滑动距离，将distance调整为刚好使某个item居中显示
     */
    private fun calculateFlingDistance(velocityY: Int, distance: Double): Double {
        val extra = mScrollY % getIntervalHeight()
        var result = distance
        if (velocityY > 0) {
            //手指由下向上滑，底部向上走
            if (distance < getIntervalHeight()) {
                //此时extra就是到达下一个item中心还需要的距离
                result = extra.toDouble()
            } else {
                result = distance - distance % getIntervalHeight() + extra
            }
        } else if (velocityY < 0) {
            if (distance < getIntervalHeight()) {
                //此时extra是额外滑动的距离，还需要getIntervalHeight() - extra到达下一个item中心
                result = (getIntervalHeight() - extra).toDouble()
            } else {
                result = distance - distance % getIntervalHeight() + (getIntervalHeight() - extra)
            }
        } else {
            if (childCount <= 0) return result
            var minDistance = Int.MAX_VALUE
            //查找当前距离列表中心最近的view，将其移动到中心
            for (pos in 0 until itemCount) {
                val itemRect = getItemCurLayout(pos)
                val curDistance = itemRect.centerY() - getVerticalSpace() / 2
                if (Math.abs(curDistance) < Math.abs(minDistance)) {
                    minDistance = curDistance
                }
            }
            // 误差小于5px就不处理了
            if (Math.abs(minDistance) < 5) return result
            if (minDistance > 0) {
                result = extra.toDouble()
            } else if (minDistance < 0) {
                result = (getIntervalHeight() - extra).toDouble()
            }
        }

        return result
    }


    override fun onAdapterChanged(
        oldAdapter: RecyclerView.Adapter<*>?,
        newAdapter: RecyclerView.Adapter<*>?
    ) {
        removeAllViews()
        mRecycler = null
        mState = null
    }

    override fun scrollToPosition(position: Int) {
        if (position < 0 || position > itemCount - 1) return
        mScrollY = calculateOffsetForPosition(position)
        if (mRecycler == null || mState == null) { //如果RecyclerView还没初始化完，先记录下要滚动的位置
            mSelectPosition = position
        } else {
            mRecycler?.let {
                val direction =
                    if (position > mSelectPosition) ITEM_FROM_BOTTOM_TO_TOP else ITEM_FROM_TOP_TO_BOTTOM
                layoutItems(
                    it,
                    mState,
                    direction
                )
                onSelectChangeCallBack(direction)
            }
        }
    }

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView?,
        state: RecyclerView.State?,
        position: Int
    ) {
        val finalOffset = calculateOffsetForPosition(position)
        if (mRecycler == null || mState == null) { //如果RecyclerView还没初始化完，先记录下要滚动的位置
            mSelectPosition = position
        } else {
            startScroll(mScrollY, finalOffset)
        }
    }

    fun findFirstVisibleItemPosition():Int {
        for (i in 0 until childCount) {
            val view = getChildAt(i) ?: continue
            val pos = getPosition(view)
            if (isItemVisible(pos)) {
                return pos
            }
        }
        return RecyclerView.NO_POSITION
    }

    fun findFirstCompletelyVisibleItemPosition(): Int {
        for (i in 0 until childCount) {
            val view = getChildAt(i) ?: continue
            val pos = getPosition(view)
            if (isItemCompletelyVisible(pos)) {
                return pos
            }
        }
        return RecyclerView.NO_POSITION
    }

    fun findLastVisibleItemPosition():Int {
        for (i in childCount - 1 downTo 0) {
            val view = getChildAt(i) ?: continue
            val pos = getPosition(view)
            if (isItemVisible(pos)) {
                return pos
            }
        }
        return RecyclerView.NO_POSITION
    }

    fun findLastCompletelyVisibleItemPosition(): Int {
        for (i in childCount - 1 downTo 0) {
            val view = getChildAt(i) ?: continue
            val pos = getPosition(view)
            if (isItemCompletelyVisible(pos)) {
                return pos
            }
        }
        return RecyclerView.NO_POSITION
    }

    /**
     * 设置选中监听
     * @param l 监听接口
     */
    fun setOnSelectedListener(listener: OnSelectedChangeListener) {
        selectChangeListener = listener
    }

    /**
     * 选中监听接口
     */
    interface OnSelectedChangeListener {
        /**
         * 监听选中回调
         * @param oldPosition 上一个显示在中间的item位置
         * @param newPosition 当前显示在中间的Item的位置
         */
        fun onItemChanged(oldPosition: Int, newPosition: Int)
    }
}
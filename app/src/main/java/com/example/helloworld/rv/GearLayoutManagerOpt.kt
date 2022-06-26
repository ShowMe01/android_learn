package com.example.helloworld.rv

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.animation.AccelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.log

/**
 * 布局规则：所有item自底向上、靠右布局
 */
class GearLayoutManagerOpt : RecyclerView.LayoutManager() {
    private val TAG = "GearLayoutManager"

    private var mVisibleArea = Rect()

    /**无方向*/
    private val ITEM_DIRECTION_NONE = 0

    /**item 自底向上移动 */
    private val ITEM_DIRECTION_UP = 1

    /**item 自顶向下移动*/
    private val ITEM_DIRECTION_DOWN = 2

    /**
     * 当前滑动量，向下为正，和view同方向
     */
    private var mScrollY = 0

    /**
     * 初始时，底部item向上的偏移量，保证首个item在列表中央
     */
    private var mStartY = 0

    /**正显示在中间的Item */
    private var mSelectPosition = 0

    /**前一个正显示在中间的Item */
    private var mLastSelectPosition = 0

    /**滚动动画 */
    private var mAnimation: ValueAnimator? = null

    /**由中心向两边尝试检查是否需要绘制的item数*/
    private val MAX_CHECK_COUNT = 3

    /**边缘预加载item数量*/
    private val MARGIN_PRELOAD_COUNT = 1

    /**选中监听 */
    private var selectChangeListener: OnSelectedChangeListener? = null

    /**item宽高*/
    private var mDecoratedChildWidth = 0
    private var mDecoratedChildHeight = 0

    /**邻近item中心之间的距离*/
    private var mIntervalHeight = 0

    /**列表布局中同时最多可见的item数*/
    private var mVisibleRowCount = 0

    /** 首个可见的item位置 */
    private var mFirstVisiblePosition = 0

    private val mItemTempRect = Rect()

    /**
     * TODO：
     *  1.支持布局反转
     *  2.支持item增删动画
     */
    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        //如果没有item，直接返回
        if (itemCount == 0) {
            detachAndScrapAttachedViews(recycler)
            return
        }

        //跳过preLayout，preLayout主要用于支持动画
        if (itemCount <= 0 || state.isPreLayout) {
            mScrollY = 0
            return
        }

        if (childCount == 0) {
            //测量item大小
            val scrap = recycler.getViewForPosition(0)
            addView(scrap)
            measureChildWithMargins(scrap, 0, 0)

            mDecoratedChildWidth = getDecoratedMeasuredWidth(scrap)
            mDecoratedChildHeight = getDecoratedMeasuredHeight(scrap)

            detachAndScrapView(scrap, recycler)
        }

        mStartY = getVerticalSpace() / 2 - mDecoratedChildHeight / 2

        //计算可见item行数
        updateWindowSizing()

        val childRight: Int
        val childStartBottom: Int
        if (childCount == 0) { //First or empty layout
            //Reset the visible and scroll positions
            mFirstVisiblePosition = 0
            childStartBottom = getChildStartBottom()
            childRight = width - paddingRight
        } else if (!state.isPreLayout && mVisibleRowCount >= state.itemCount) {
            //item数太少装不满列表
            mFirstVisiblePosition = 0
            childRight = width - paddingRight
            childStartBottom = getChildStartBottom()
        } else {
            //Adapter data set changes
            val firstChild = getChildAt(0) ?: return
            childStartBottom = getDecoratedBottom(firstChild)
            childRight = getDecoratedRight(firstChild)
        }

        Log.d(TAG, "onLayoutChildren: detachAndScrapAttachedViews state:${state}")

        //Clear all attached views into the recycle bin
        detachAndScrapAttachedViews(recycler)

        //Fill the grid for the initial layout of views
        fill(ITEM_DIRECTION_NONE, childRight, childStartBottom, recycler, state)

        //Evaluate any disappearing views that may exist
        //当添加新view时，可能会有view被挤到看不见的位置，这些view需要被移除
        if (!state.isPreLayout && recycler.scrapList.isNotEmpty()) {
            val scrapList = recycler.scrapList
            val disappearingViews = HashSet<View>(scrapList.size)
            for (holder in scrapList) {
                val child = holder.itemView
                val lp = child.layoutParams as LayoutParams
                if (!lp.isItemRemoved) {
                    disappearingViews.add(child)
                }
            }
            for (child in disappearingViews) {
                layoutDisappearingView(child)
            }
        }
    }

    /**
     * 首个item居中显示
     */
    private fun getChildStartBottom(): Int {
        return height - paddingBottom - mStartY
    }

    /* Helper to place a disappearing view */
    private fun layoutDisappearingView(disappearingChild: View) {
        /*
         * LayoutManager has a special method for attaching views that
         * will only be around long enough to animate.
         */
        addDisappearingView(disappearingChild)

        //Adjust each disappearing view to its proper place
        val lp = disappearingChild.layoutParams as LayoutParams
        val newRow: Int = lp.viewAdapterPosition
        val rowDelta = newRow - lp.rowIndex
        layoutTempChildView(disappearingChild, rowDelta, disappearingChild)
    }

    /**
     * 以referenceView为参考点，为child布局
     */
    private fun layoutTempChildView(
        child: View,
        rowDelta: Int,
        referenceView: View
    ) {
        //Set the layout position to the global row/column difference from the reference view
        val layoutTop = getDecoratedTop(referenceView) + rowDelta * getIntervalHeight()
        val layoutLeft = getDecoratedLeft(referenceView)
        measureChildWithMargins(child, 0, 0)
        layoutDecorated(
            child, layoutLeft,
            layoutTop,
            layoutLeft + mDecoratedChildWidth,
            layoutTop + mDecoratedChildHeight
        )
        handleViewTransform(child, layoutTop + mDecoratedChildHeight / 2 - getVerticalSpace() / 2)
        Log.d(TAG, "layoutTempChildView: pos:${getPosition(child)} ")
    }

    private fun fill(
        direction: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ) {
        fill(direction, width, height, recycler, state)
    }

    /**
     * 1.detach当前所有child，暂存到集合中
     * 2.对即将可见的view添加到Recyclerview中，若在集合中，则移除
     * 3.集合中只剩下不可见view，进行回收
     */
    private fun fill(
        direction: Int,
        emptyRight: Int,
        emptyBottom: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ) {
        if (mFirstVisiblePosition < 0) mFirstVisiblePosition = 0
        if (mFirstVisiblePosition >= itemCount) mFirstVisiblePosition = itemCount - 1

        /*
         * First, we will detach all existing views from the layout.
         * detachView() is a lightweight operation that we can use to
         * quickly reorder views without a full add/remove.
         */
        val viewCache = SparseArray<View>(childCount)
        var startBottomOffset = emptyBottom
        if (childCount != 0) {
            val firstView = getChildAt(0) ?: return
            startBottomOffset = getDecoratedBottom(firstView)
            when (direction) {
                ITEM_DIRECTION_UP -> startBottomOffset += getIntervalHeight()
                ITEM_DIRECTION_DOWN -> startBottomOffset -= getIntervalHeight()
            }

            //Cache all views by their existing position, before updating counts
            for (i in 0 until childCount) {
                val position: Int = adapterPositionOfChildIndex(i)
                val child = getChildAt(i) ?: continue
                viewCache.put(position, child)
            }

            //Temporarily detach all views.
            // Views we still need will be added back at the proper index.
            for (i in 0 until viewCache.size()) {
                detachView(viewCache.valueAt(i))
            }
        }

        /*
         * Next, we advance the visible position based on the fill direction.
         * DIRECTION_NONE doesn't advance the position in any direction.
         */
        when (direction) {
            ITEM_DIRECTION_UP -> mFirstVisiblePosition--
            ITEM_DIRECTION_DOWN -> mFirstVisiblePosition++
        }

        /*
         * Next, we supply the grid of items that are deemed visible.
         * If these items were previously there, they will simply be
         * re-attached. New views that must be created are obtained
         * from the Recycler and added.
         */
        var bottomOffset = startBottomOffset

        for (i in 0 until getVisibleChildCount()) {
            val nextPosition: Int = adapterPositionOfChildIndex(i)
            val offsetPositionDelta = 0
            if (nextPosition < 0 || nextPosition >= state.itemCount) {
                //Item space beyond the data set, don't attempt to add a view
                continue
            }

            //Layout this position
            var view = viewCache.get(nextPosition)
            if (view == null) {
                //从回收站获取view，这个view可能是新创建(onCreateViewHolder)，也可能是废弃view(onBindViewHolder)
                view = recycler.getViewForPosition(nextPosition)
                addView(view)

                if (!state.isPreLayout) {
                    val lp = view.layoutParams as LayoutParams
                    lp.rowIndex = nextPosition
                }

                //新添加的view需要测量、布局
                measureChildWithMargins(view, 0, 0)
                layoutDecorated(
                    view,
                    emptyRight - mDecoratedChildWidth,
                    bottomOffset - mDecoratedChildHeight,
                    emptyRight,
                    bottomOffset
                )
                handleViewTransform(
                    view,
                    bottomOffset - mDecoratedChildHeight / 2 - getVerticalSpace() / 2
                )
            } else {
                //Re-attach the cached view at its new index
                attachView(view)
                viewCache.remove(nextPosition)
            }

            bottomOffset -= getIntervalHeight()
        }

        /*
         * Finally, we ask the Recycler to scrap and store any views
         * that we did not re-attach. These are views that are not currently
         * necessary because they are no longer visible.
         */
        for (i in 0 until viewCache.size()) {
            val removingView = viewCache.valueAt(i)
            recycler.recycleView(removingView)
        }

    }

    private fun getVisibleChildCount(): Int {
        return mVisibleRowCount
    }

    private fun adapterPositionOfChildIndex(childIndex: Int): Int {
        return mFirstVisiblePosition + childIndex
    }

    private fun updateWindowSizing() {
        mVisibleRowCount = getVerticalSpace() / getIntervalHeight() + 1
        if (getVerticalSpace() % getIntervalHeight() > 0) {
            mVisibleRowCount++
        }
        if (mVisibleRowCount > getTotalRowCount()) {
            mVisibleRowCount = getTotalRowCount()
        }
    }

    private fun getTotalRowCount(): Int {
        return itemCount
    }

    private fun onSelectChangeCallBack() {
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
        if (childCount == 0 || itemCount <= 0) {
            return 0
        }

        if (mAnimation != null && mAnimation?.isRunning == true) {
            mAnimation?.cancel()
        }

        val topView = getChildAt(childCount - 1) ?: return 0
        val bottomView = getChildAt(0) ?: return 0

        //item填不满列表，不能滑动
        val viewSpan = getDecoratedBottom(bottomView) - getDecoratedTop(topView)
        if (viewSpan < getVerticalSpace()) {
            return 0
        }

        val delta: Int
        val maxRowCount = getTotalRowCount()
        val topBoundReached = getLastVisibleRowIndex() >= maxRowCount - 1
        val bottomBoundReached = getFirstVisibleRowIndex() == 0

        if (dy > 0) {
            delta = if (bottomBoundReached) {
                //If we've reached the last row, enforce limits
                val maxBottomOffset =
                    getDecoratedBottom(bottomView) - (getVerticalSpace() / 2 + mDecoratedChildHeight / 2)
                -Math.min(dy, maxBottomOffset)
            } else {
                -dy
            }
        } else {
            delta = if (topBoundReached) {
                val maxTopOffset =
                    getVerticalSpace() / 2 - mDecoratedChildHeight / 2 - getDecoratedTop(topView)
                Math.min(-dy, maxTopOffset)
            } else {
                -dy
            }
        }

        offsetViewAndTransform(delta)

        val visibleArea = getVisibleArea()
        if (dy > 0) {
            if (getDecoratedBottom(bottomView) < visibleArea.bottom && !bottomBoundReached) {
                fill(ITEM_DIRECTION_UP, recycler, state)
            } else if (!bottomBoundReached) {
                fill(ITEM_DIRECTION_NONE, recycler, state)
            }
        } else {
            if (getDecoratedTop(bottomView) > visibleArea.bottom/* && !topBoundReached*/) {
                fill(ITEM_DIRECTION_DOWN, recycler, state)
            } else if (!topBoundReached) {
                fill(ITEM_DIRECTION_NONE, recycler, state)
            }
        }

        return -delta
    }

    private fun getFirstVisibleRowIndex(): Int {
        return mFirstVisiblePosition
    }

    private fun getLastVisibleRowIndex(): Int {
        return mFirstVisiblePosition + mVisibleRowCount - 1
    }

    private fun getVerticalMaxOffset(): Int {
        return if (itemCount < 0) {
            0
        } else {
            (itemCount - 1) * getIntervalHeight()
        }
    }

    private fun getIntervalHeight(): Int {
        return (mDecoratedChildHeight * 0.93f).toInt()
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

    private fun offsetViewAndTransform(dy: Int) {
        mScrollY += dy
        offsetChildrenVertical(dy)
        handleAllViewTransform()
        onSelectChangeCallBack()
    }

    private fun handleAllViewTransform() {
        for (i in 0 until childCount) {
            val view = getChildAt(i) ?: continue
            handleViewTransform(
                view,
                (view.top + view.bottom) / 2 - getVerticalSpace() / 2
            )
        }
    }

    /**
     * @param centerOffsetY 到列表中心水平线的距离
     */
    private fun handleViewTransform(itemView: View, centerOffsetY: Int) {
        itemView.post {
            //缩放
            val ratio = Math.abs(centerOffsetY * 1f / getIntervalHeight())
            val scale = TransformUtils.mix(1f, 0.6f, ratio)
            itemView.scaleX = scale
            itemView.scaleY = scale

            //旋转
            val rotateRatio = Math.abs(centerOffsetY * 1f / getIntervalHeight())
            var angle = TransformUtils.mix(0f, 10f, rotateRatio)
            if (centerOffsetY > 0) {
                angle = -angle
            }
            itemView.rotation = angle
            //平移
            val tRatio = Math.abs(centerOffsetY * 1f / getIntervalHeight())
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
            getHorizontalSpace() - mDecoratedChildWidth,
            getVerticalSpace() - position * getIntervalHeight() - mDecoratedChildHeight - mStartY + offsetY,
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
        var flingY = (velocityY * 0.6f).toInt()
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
            startScrollAnim(mScrollY, finalOffset)
        }
    }

    /**
     * 滚动到指定X轴位置
     * @param from Y轴方向起始点的偏移量
     * @param to Y轴方向终点的偏移量
     */
    private fun startScrollAnim(from: Int, to: Int) {
        if (mAnimation?.isRunning == true) {
            mAnimation?.cancel()
        }
        val direction = if (from < to) ITEM_DIRECTION_DOWN else ITEM_DIRECTION_UP
        mAnimation = ValueAnimator.ofFloat(from.toFloat(), to.toFloat()).apply {
            duration = 300
            interpolator = AccelerateInterpolator()
            addUpdateListener {
                if (it.animatedValue is Float) {
                    val dy = Math.round(it.animatedValue as Float) - mScrollY
                    offsetViewAndTransform(dy)
                }
            }
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
    }

    override fun scrollToPosition(position: Int) {
        if (position >= itemCount) {
            return
        }

        //Set requested position as first visible
        mFirstVisiblePosition = position
        //Toss all existing views away
        removeAllViews()
        //Trigger a new view layout
        requestLayout()
        onSelectChangeCallBack()
    }

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView,
        state: RecyclerView.State,
        position: Int
    ) {
        if (position >= itemCount) {
            return
        }

        val finalOffset = calculateOffsetForPosition(position)
        startScrollAnim(mScrollY, finalOffset)

        /* val scroller: LinearSmoothScroller = object : LinearSmoothScroller(recyclerView.context) {
             *//*
             * LinearSmoothScroller, at a minimum, just need to know the vector
             * (x/y distance) to travel in order to get from the current positioning
             * to the target.
             *//*
            override fun computeScrollVectorForPosition(targetPosition: Int): PointF {
                val dy = calculateOffsetForPosition(targetPosition) - mStartY
                return PointF(0f,
                    dy.toFloat()
                )
            }
        }
        scroller.targetPosition = position
        startSmoothScroll(scroller)*/
    }

    fun findFirstVisibleItemPosition(): Int {
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

    fun findLastVisibleItemPosition(): Int {
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

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun generateLayoutParams(
        c: Context?,
        attrs: AttributeSet?
    ): RecyclerView.LayoutParams {
        return LayoutParams(c, attrs)
    }

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams?): RecyclerView.LayoutParams {
        return if (lp is MarginLayoutParams) {
            LayoutParams(lp as MarginLayoutParams?)
        } else {
            LayoutParams(lp)
        }
    }

    override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
        return lp is LayoutParams
    }

    inner class LayoutParams : RecyclerView.LayoutParams {
        var rowIndex = RecyclerView.NO_POSITION

        constructor(c: Context?, attrs: AttributeSet?) : super(c, attrs)
        constructor(width: Int, height: Int) : super(width, height)
        constructor(source: ViewGroup.MarginLayoutParams?) : super(source)
        constructor(source: ViewGroup.LayoutParams?) : super(source)
        constructor(source: RecyclerView.LayoutParams?) : super(source)
    }
}




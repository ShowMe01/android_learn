package com.example.helloworld.rv

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.recyclerview.widget.RecyclerView

class GalleyRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {

    init {
        isChildrenDrawingOrderEnabled = true
    }

    private val TAG = "GalleyRecyclerView"

    override fun getChildDrawingOrder(childCount: Int, i: Int): Int {
        Log.d(TAG, "getChildDrawingOrder: ")
        val centerPos =
            getGalleryLayoutManager().getCenterPosition() - getGalleryLayoutManager().getFirstPosition()

        return if (i < centerPos) {
            i
        } else {
            centerPos + childCount - 1 - i
        }
    }

    private fun getGalleryLayoutManager(): GalleryLayoutManager {
        return layoutManager as GalleryLayoutManager
    }

    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        //缩小滚动距离
        Log.d(TAG, "fling: init velocityX:${velocityX}")
        var flingX = (velocityX * 0.7f).toInt()
        val manger = getGalleryLayoutManager()
        val distance: Double = FlingUtils.getSplineFlingDistance(flingX)
        val newDistance = manger.calculateDistance(velocityX, distance)
        val fixVelocityX: Int = FlingUtils.getVelocity(newDistance)
        flingX = if (velocityX > 0) {
            fixVelocityX
        } else {
            -fixVelocityX
        }
        return super.fling(flingX, velocityY)
    }

}
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


}
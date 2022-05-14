package com.example.helloworld.layoutinflater

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.example.helloworld.R
import kotlin.math.log

class BtnAdapter : RecyclerView.Adapter<BtnAdapter.ViewHolder>() {

    companion object {
        private const val TAG = "Adapter"
    }

    lateinit var parent:ViewGroup
    var hasInitParent = false
    val fruitList = mutableListOf("Apple", "Banana", "Orange", "sdadada", "qiowjoda", "sakdadlas")

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btn = itemView.findViewById<Button>(R.id.btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_button, parent, false)
        if (!hasInitParent) {
            this.parent = parent
            hasInitParent = true
        }
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.btn.text = "btn_${fruitList[position % (fruitList.size)]}"
        Log.d(
            TAG,
            "onBindViewHolder: position:$position, rv childCount:${parent.childCount}"
        )
    }

    override fun getItemCount(): Int {
        return 100
    }

}
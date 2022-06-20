package com.example.helloworld.rv

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.helloworld.R
import com.example.helloworld.databinding.ItemTextCententBinding

class RvAdapter(val data: List<String>) : RecyclerView.Adapter<RvAdapter.ViewHolder>() {

    private val TAG = "RvAdapter"
    private var viewHolderCount = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d(TAG, "onCreateViewHolder: count:${++viewHolderCount}")
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_text_centent, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: position:${position}")
        holder.viewBinding.tvTitle.text = data[position]
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val viewBinding = ItemTextCententBinding.bind(itemView)
    }
}
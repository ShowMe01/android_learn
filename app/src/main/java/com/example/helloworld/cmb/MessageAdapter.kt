package com.example.helloworld.cmb

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.helloworld.R

class MessageAdapter(private val messages: MutableList<MessageItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_FROM = 1
        private const val VIEW_TYPE_TO = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].fromMe) VIEW_TYPE_FROM else VIEW_TYPE_TO
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_FROM) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_from, parent, false)
            FromViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_to, parent, false)
            ToViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position].message
        if (holder is FromViewHolder) {
            holder.bind(message)
        } else if (holder is ToViewHolder) {
            holder.bind(message)
        }
    }

    override fun getItemCount(): Int = messages.size

    fun addMessage(messageItem: MessageItem) {
        messages.add(messageItem)
        notifyItemInserted(messages.size - 1)
    }

    class FromViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)

        fun bind(message: String) {
            messageTextView.text = message
        }
    }

    class ToViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)

        fun bind(message: String) {
            messageTextView.text = message
        }
    }
}

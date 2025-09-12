package com.app.eventplannerapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.eventplannerapp.R
import com.app.eventplannerapp.data.entity.EventEntity
import java.text.DateFormat

class EventAdapter(
    private val onClick: (EventEntity) -> Unit,
    private val onLongClick: (EventEntity) -> Unit
) : ListAdapter<EventEntity, EventAdapter.EventViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position), onClick, onLongClick)
    }

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.tvTitle)
        private val description: TextView = itemView.findViewById(R.id.tvDescription)
        private val time: TextView = itemView.findViewById(R.id.tvTime)

        fun bind(item: EventEntity, onClick: (EventEntity) -> Unit, onLongClick: (EventEntity) -> Unit) {
            title.text = item.title
            description.text = item.description
            time.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(item.startTimeMillis)
            itemView.setOnClickListener { onClick(item) }
            itemView.setOnLongClickListener { onLongClick(item); true }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<EventEntity>() {
            override fun areItemsTheSame(oldItem: EventEntity, newItem: EventEntity): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: EventEntity, newItem: EventEntity): Boolean = oldItem == newItem
        }
    }
}



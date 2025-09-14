package com.app.eventplannerapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.eventplannerapp.data.entity.EventEntity
import com.app.eventplannerapp.databinding.ItemEventBinding
import java.text.DateFormat

class EventAdapter(
    private val onClick: (EventEntity) -> Unit,
    private val onLongClick: (EventEntity) -> Unit
) : ListAdapter<EventEntity, EventAdapter.EventViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position), onClick, onLongClick)
    }

    class EventViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: EventEntity, onClick: (EventEntity) -> Unit, onLongClick: (EventEntity) -> Unit) {
            binding.tvTitle.text = item.title
            binding.tvDescription.text = item.description
            binding.tvTime.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(item.startTimeMillis)
            binding.root.setOnClickListener { onClick(item) }
            binding.root.setOnLongClickListener { onLongClick(item); true }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<EventEntity>() {
            override fun areItemsTheSame(oldItem: EventEntity, newItem: EventEntity): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: EventEntity, newItem: EventEntity): Boolean = oldItem == newItem
        }
    }
}
package com.app.eventplannerapp.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.eventplannerapp.data.entity.EventEntity
import com.app.eventplannerapp.databinding.ItemUpcomingEventFullBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UpcomingEventAdapter(
    private val onClick: (EventEntity) -> Unit,
    private val onLongClick: (EventEntity) -> Unit
) : RecyclerView.Adapter<UpcomingEventAdapter.UpcomingEventViewHolder>() {

    private var events: List<EventEntity> = emptyList()

    fun submitList(newEvents: List<EventEntity>) {
        events = newEvents
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingEventViewHolder {
        val binding = ItemUpcomingEventFullBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UpcomingEventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UpcomingEventViewHolder, position: Int) {
        holder.bind(events[position], onClick, onLongClick)
    }

    override fun getItemCount(): Int = events.size

    class UpcomingEventViewHolder(private val binding: ItemUpcomingEventFullBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: EventEntity, onClick: (EventEntity) -> Unit, onLongClick: (EventEntity) -> Unit) {
            binding.tvUpcomingTitle.text = "Title: "+event.title
            binding.tvUpcomingDescription.text = "Description: "+event.description

            val cal = Calendar.getInstance().apply { timeInMillis = event.startTimeMillis }
            val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
            val timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT)

            val dateString = dateFormat.format(event.startTimeMillis)
            val timeString = timeFormat.format(event.startTimeMillis)
            binding.tvUpcomingDateTime.text = "$dateString at $timeString"

            // Calculate time remaining
            binding.tvTimeRemaining?.let { timeRemainingView ->
                val now = System.currentTimeMillis()
                val timeDiff = event.startTimeMillis - now

                when {
                    timeDiff < 0 -> {
                        timeRemainingView.text = "Event has passed"
                        timeRemainingView.setTextColor(Color.RED)
                    }
                    timeDiff < 24 * 60 * 60 * 1000 -> { // Less than 24 hours
                        val hours = timeDiff / (60 * 60 * 1000)
                        timeRemainingView.text = "In $hours hours"
                        timeRemainingView.setTextColor(Color.parseColor("#FF6B35"))
                    }
                    timeDiff < 7 * 24 * 60 * 60 * 1000 -> { // Less than 7 days
                        val days = timeDiff / (24 * 60 * 60 * 1000)
                        timeRemainingView.text = "In $days days"
                        timeRemainingView.setTextColor(Color.parseColor("#4CAF50"))
                    }
                    else -> {
                        val days = timeDiff / (24 * 60 * 60 * 1000)
                        timeRemainingView.text = "In $days days"
                        timeRemainingView.setTextColor(Color.parseColor("#2196F3"))
                    }
                }
            }

            binding.root.setOnClickListener { onClick(event) }
            binding.root.setOnLongClickListener { onLongClick(event); true }
        }
    }
}
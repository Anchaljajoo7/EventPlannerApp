package com.app.eventplannerapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.eventplannerapp.R
import java.time.LocalDate

class CalendarGridAdapter(
    private var days: List<LocalDate>,
    private var eventDates: Set<LocalDate>,
    private val onDayClick: (LocalDate) -> Unit,
) : RecyclerView.Adapter<CalendarGridAdapter.DayViewHolder>() {

    private var selectedDate: LocalDate? = null

    fun updateDays(newDays: List<LocalDate>) {
        days = newDays
        notifyDataSetChanged()
    }

    fun updateEventDates(newEventDates: Set<LocalDate>) {
        eventDates = newEventDates
        notifyDataSetChanged()
    }

    fun setSelectedDate(date: LocalDate?) {
        selectedDate = date
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.day_calendar_cell, parent, false)
        return DayViewHolder(v)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val date = days[position]
        holder.bind(date, eventDates.contains(date), onDayClick)
        holder.setSelected(date == selectedDate)
    }

    override fun getItemCount(): Int = days.size

    class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDay: TextView = itemView.findViewById(R.id.tvDayNumber)
        private val indicator: View = itemView.findViewById(R.id.indicator)

        fun bind(date: LocalDate, hasEvent: Boolean, onDayClick: (LocalDate) -> Unit) {
            tvDay.text = date.dayOfMonth.toString()
            indicator.visibility = if (hasEvent) View.VISIBLE else View.GONE
            itemView.setOnClickListener { onDayClick(date) }
            tvDay.background = null
        }
        fun setSelected(selected: Boolean) {
            tvDay.background = if (selected) itemView.context.getDrawable(R.drawable.bg_selected_day) else null
        }
    }
}


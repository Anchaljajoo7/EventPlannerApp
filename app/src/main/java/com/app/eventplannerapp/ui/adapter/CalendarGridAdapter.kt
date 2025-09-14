package com.app.eventplannerapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.eventplannerapp.R
import java.time.LocalDate
import java.time.YearMonth

class CalendarGridAdapter(
    private var days: List<LocalDate>,
    private var eventDates: Set<LocalDate>,
    private val onDayClick: (LocalDate) -> Unit,
    private var currentMonth: YearMonth
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

    fun updateCurrentMonth(month: YearMonth) {
        currentMonth = month
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.day_calendar_cell, parent, false)
        return DayViewHolder(v)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val date = days[position]
        val isSelected = date == selectedDate
        holder.bind(date, eventDates.contains(date), onDayClick, currentMonth, isSelected)
    }

    override fun getItemCount(): Int = days.size

    class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDay: TextView = itemView.findViewById(R.id.tvDayNumber)
        private val indicator: View = itemView.findViewById(R.id.indicator)

        fun bind(
            date: LocalDate,
            hasEvent: Boolean,
            onDayClick: (LocalDate) -> Unit,
            currentMonth: YearMonth,
            isSelected: Boolean
        ) {
            tvDay.text = date.dayOfMonth.toString()
            indicator.visibility = if (hasEvent) View.VISIBLE else View.GONE
            itemView.setOnClickListener { onDayClick(date) }
            tvDay.background = null

            // Set different text colors for current month vs other months
            val isCurrentMonth = date.year == currentMonth.year && date.month == currentMonth.month
            tvDay.setTextColor(
                if (isCurrentMonth)
                    itemView.context.getColor(android.R.color.black)
                else
                    itemView.context.getColor(android.R.color.darker_gray)
            )

            // Highlight today
            val today = LocalDate.now()
            tvDay.background = when {
                isSelected -> itemView.context.getDrawable(R.drawable.bg_selected_day)
                date == today -> itemView.context.getDrawable(R.drawable.bg_today_day)
                else -> null
            }
        }
    }
}
package com.app.eventplannerapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import com.app.eventplannerapp.R

class MonthPagerAdapter(
    private val initialMonth: YearMonth,
    private val getEventDates: () -> Set<LocalDate>,
    private val onDayClick: (LocalDate) -> Unit
) : RecyclerView.Adapter<MonthPagerAdapter.MonthViewHolder>() {

    // We render a large range centered on initialMonth
    private val totalMonths = 1200 // ~100 years
    private val startIndex = totalMonths / 2
    private var selectedDate: LocalDate? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.view_month_grid, parent, false)
        return MonthViewHolder(v)
    }

    override fun getItemCount(): Int = totalMonths

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        val month = initialMonth.plusMonths((position - startIndex).toLong())
        holder.bind(month, getEventDates(), selectedDate, onDayClick)
    }

    fun getCenterPosition(): Int = startIndex

    fun setSelectedDate(date: LocalDate?) {
        selectedDate = date
        notifyDataSetChanged()
    }

    class MonthViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val rv: RecyclerView = itemView.findViewById(R.id.monthGrid)
        private val tvMonth: TextView = itemView.findViewById(R.id.tvMonthTitle)
        private var adapter: CalendarGridAdapter? = null

        fun bind(month: YearMonth, eventDates: Set<LocalDate>, selectedDate: LocalDate?, onDayClick: (LocalDate) -> Unit) {
//            tvMonth.text = "${month.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${month.year}"

            val first = month.atDay(1)
            val daysInMonth = month.lengthOfMonth()
            val startShift = (first.dayOfWeek.ordinal + 7 - DayOfWeek.MONDAY.ordinal) % 7
            val days = buildList {
                // Leading days from previous month to align the first week starting Monday
                for (i in startShift downTo 1) add(first.minusDays(i.toLong()))
                // All days in current month
                repeat(daysInMonth) { add(first.plusDays(it.toLong())) }
                // Trailing days from next month to fill the last week
                val remainder = size % 7
                if (remainder != 0) {
                    val toFill = 7 - remainder
                    val startNext = first.plusDays(daysInMonth.toLong())
                    repeat(toFill) { add(startNext.plusDays(it.toLong())) }
                }
            }

            if (adapter == null) {
                adapter = CalendarGridAdapter(days, eventDates, onDayClick, month)
                rv.layoutManager = GridLayoutManager(itemView.context, 7)
                rv.adapter = adapter
            } else {
                adapter!!.updateDays(days)
                adapter!!.updateEventDates(eventDates)
                adapter!!.updateCurrentMonth(month)
            }
            adapter!!.setSelectedDate(selectedDate)
        }
    }
}


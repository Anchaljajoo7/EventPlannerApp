package com.app.eventplannerapp

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.eventplannerapp.data.entity.EventEntity
import com.app.eventplannerapp.ui.AddEditEventDialog
import com.app.eventplannerapp.ui.EventAdapter
import com.app.eventplannerapp.ui.EventViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.viewpager2.widget.ViewPager2
import com.app.eventplannerapp.ui.CalendarGridAdapter
import com.app.eventplannerapp.ui.MonthPagerAdapter
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private val viewModel: EventViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val monthPager = findViewById<ViewPager2>(R.id.monthPager)
        val btnPrev = findViewById<ImageButton>(R.id.btnPrev)
        val btnNext = findViewById<ImageButton>(R.id.btnNext)
        val tvMonthLabel = findViewById<TextView>(R.id.tvMonthLabel)
        val recycler = findViewById<RecyclerView>(R.id.rvEvents)
        val fab = findViewById<FloatingActionButton>(R.id.fabAdd)

        val adapter = EventAdapter(
            onClick = { event -> showAddEditDialog(event) },
            onLongClick = { event -> viewModel.deleteEvent(event) }
        )
        recycler.adapter = adapter

        var shownMonth = YearMonth.now()
        fun updateHeader() {
            tvMonthLabel.text = "${shownMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${shownMonth.year}"
        }
        updateHeader()
        var cachedEventDates: Set<LocalDate> = emptySet()
        var monthAdapter: MonthPagerAdapter? = null
        val onDayClick: (LocalDate) -> Unit = { date ->
            val cal = Calendar.getInstance().apply {
                set(date.year, date.monthValue - 1, date.dayOfMonth, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            viewModel.setSelectedDate(cal.timeInMillis)
            monthAdapter?.setSelectedDate(date)
        }
        monthAdapter = MonthPagerAdapter(YearMonth.now(), { cachedEventDates }, onDayClick)
        monthPager.adapter = monthAdapter
        monthPager.setCurrentItem(monthAdapter.getCenterPosition(), false)

        btnPrev.setOnClickListener { monthPager.currentItem = monthPager.currentItem - 1 }
        btnNext.setOnClickListener { monthPager.currentItem = monthPager.currentItem + 1 }

        monthPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val month = YearMonth.now().plusMonths((position - (monthAdapter?.getCenterPosition() ?: 0)).toLong())
                shownMonth = month
                updateHeader()
            }
        })

        // Observe all events and update indicators
        viewModel.allEvents.observe(this) { events ->
            val eventDates = events.map {
                val cal = java.util.Calendar.getInstance().apply { timeInMillis = it.startTimeMillis }
                LocalDate.of(
                    cal.get(java.util.Calendar.YEAR),
                    cal.get(java.util.Calendar.MONTH) + 1,
                    cal.get(java.util.Calendar.DAY_OF_MONTH)
                )
            }.toSet()
            cachedEventDates = eventDates
            monthAdapter?.notifyDataSetChanged()
        }

        viewModel.eventsForSelectedDay.observe(this) { events ->
            adapter.submitList(events)
        }

        fab.setOnClickListener {
            showAddEditDialog(null)
        }
    }

    private fun showAddEditDialog(event: EventEntity?) {
        val initialTime = event?.startTimeMillis ?: viewModel.selectedDayMillis.value
        ?: System.currentTimeMillis()
        AddEditEventDialog(
            initialTitle = event?.title,
            initialDescription = event?.description,
            initialTimeMillis = initialTime,
            onConfirm = { title, description, timeMillis ->
                if (event == null) {
                    viewModel.addEvent(title, description, timeMillis)
                } else {
                    viewModel.updateEvent(
                        event.copy(
                            title = title,
                            description = description,
                            startTimeMillis = timeMillis
                        )
                    )
                }
            }
        ).show(supportFragmentManager, "AddEditEventDialog")
    }
}
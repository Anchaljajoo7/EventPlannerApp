package com.app.eventplannerapp.ui.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.app.eventplannerapp.ui.view.UpcomingEventsActivity
import com.app.eventplannerapp.data.entity.EventEntity
import com.app.eventplannerapp.databinding.ActivityMainBinding
import com.app.eventplannerapp.dialog.AddEditEventDialog
import com.app.eventplannerapp.dialog.DeleteEventDialog
import com.app.eventplannerapp.ui.adapter.EventAdapter
import com.app.eventplannerapp.ui.viewmodel.EventViewModel
import com.app.eventplannerapp.ui.adapter.MonthPagerAdapter
import java.time.LocalDate
import java.time.YearMonth
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private val viewModel: EventViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val adapter = EventAdapter(
            onClick = { event -> showAddEditDialog(event) },
            onLongClick = { event -> showDeleteConfirmationDialog(event) }
        )
        binding.rvEvents.adapter = adapter

        var shownMonth = YearMonth.now()
        fun updateHeader() {
            binding.tvMonthLabel.text = "${shownMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${shownMonth.year}"
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
        binding.monthPager.adapter = monthAdapter
        binding.monthPager.setCurrentItem(monthAdapter.getCenterPosition(), false)

        binding.btnPrev.setOnClickListener { binding.monthPager.currentItem = binding.monthPager.currentItem - 1 }
        binding.btnNext.setOnClickListener { binding.monthPager.currentItem = binding.monthPager.currentItem + 1 }

        binding.monthPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
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
                val cal = Calendar.getInstance().apply { timeInMillis = it.startTimeMillis }
                LocalDate.of(
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH)
                )
            }.toSet()
            cachedEventDates = eventDates
            monthAdapter?.notifyDataSetChanged()
        }

        viewModel.eventsForSelectedDay.observe(this) { events ->
            adapter.submitList(events)
        }

        binding.fabAdd.setOnClickListener {
            showAddEditDialog(null)
        }

        binding.btnUpcomingEvents.setOnClickListener {
            val intent = Intent(this, UpcomingEventsActivity::class.java)
            startActivity(intent)
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

    private fun showDeleteConfirmationDialog(event: EventEntity) {
        DeleteEventDialog(
            event = event,
            onConfirm = { eventToDelete ->
                viewModel.deleteEvent(eventToDelete)
            }
        ).show(supportFragmentManager, "DeleteEventDialog")
    }

}
package com.app.eventplannerapp.ui.view

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.eventplannerapp.databinding.ActivityUpcomingEventsBinding
import com.app.eventplannerapp.ui.viewmodel.EventViewModel
import com.app.eventplannerapp.ui.adapter.UpcomingEventAdapter

class UpcomingEventsActivity : AppCompatActivity() {

    private val viewModel: EventViewModel by viewModels()
    private lateinit var binding: ActivityUpcomingEventsBinding
    private lateinit var adapter: UpcomingEventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUpcomingEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupToolbar()
        setupRecyclerView()
        observeUpcomingEvents()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Upcoming Events"

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = UpcomingEventAdapter(
            onClick = { event ->
                // Edit event - you can implement this later
                // For now, just show a toast or handle as needed
            },
            onLongClick = { event ->
                // Delete event - you can implement this later
                // For now, just show a toast or handle as needed
            }
        )

        binding.rvUpcomingEvents.layoutManager = LinearLayoutManager(this)
        binding.rvUpcomingEvents.adapter = adapter
    }

    private fun observeUpcomingEvents() {
        viewModel.upcomingEvents.observe(this) { events ->
            if (events.isEmpty()) {
                binding.rvUpcomingEvents.visibility = View.GONE
                binding.tvEmptyUpcoming.visibility = View.VISIBLE
                binding.tvEmptyUpcoming.text = "No upcoming events"
            } else {
                binding.rvUpcomingEvents.visibility = View.VISIBLE
                binding.tvEmptyUpcoming.visibility = View.GONE
                adapter.submitList(events)
            }
        }
    }
}
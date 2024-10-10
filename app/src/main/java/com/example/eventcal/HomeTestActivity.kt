package com.example.eventcal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventcal.adapters.EventAdapter
import com.example.eventcal.models.Event
import java.time.LocalDateTime

class HomeTestActivity : AppCompatActivity() {
    /*
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)  // Use home layout

        // Create test data
        val testEvents = listOf(
            Event("Event 1", LocalDateTime.now()),
            Event("Event 2", LocalDateTime.now().plusDays(1)),
            Event("Event 3", LocalDateTime.now().plusDays(1).plusHours(1)),
            Event("Event 4", LocalDateTime.now().plusDays(2)),
            Event("Event 5", LocalDateTime.now().plusDays(2).plusHours(1))
        )

        // Initialize RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.event_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize EventAdapter FIXME
        val adapter = EventAdapter(testEvents) { event ->
            // Handle delete button click
            println("Event deleted: ${event.title}")
        }

        recyclerView.adapter = adapter


    }
*/}

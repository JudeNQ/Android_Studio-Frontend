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
            ServerEvent("ServerEvent 1", LocalDateTime.now()),
            ServerEvent("ServerEvent 2", LocalDateTime.now().plusDays(1)),
            ServerEvent("ServerEvent 3", LocalDateTime.now().plusDays(1).plusHours(1)),
            ServerEvent("ServerEvent 4", LocalDateTime.now().plusDays(2)),
            ServerEvent("ServerEvent 5", LocalDateTime.now().plusDays(2).plusHours(1))
        )

        // Initialize RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.event_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize EventAdapter FIXME
        val adapter = EventAdapter(testEvents) { event ->
            // Handle delete button click
            println("ServerEvent deleted: ${event.title}")
        }

        recyclerView.adapter = adapter


    }
*/}

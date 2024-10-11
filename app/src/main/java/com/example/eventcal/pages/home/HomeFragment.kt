package com.example.eventcal.pages.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventcal.MainActivity
import com.example.eventcal.R
import com.example.eventcal.adapters.EventAdapter
import com.example.eventcal.databinding.FragmentHomeBinding
import com.example.eventcal.models.Event
import com.example.eventcal.pojo.ServerEvent
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment() {
    private var _binding : FragmentHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val mainActivity : MainActivity = activity as MainActivity

        //Create an empty list for the events to be added to home page
        var eventList = ArrayList<Event>()

        mainActivity.doGetEventList("10/11/2024") {
            if (it != null) {
                for (event: ServerEvent in it.data) {
                    eventList.add(
                        Event(
                            event.eventName,
                            LocalDateTime.parse(event.date, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        )
                    )
                }

                // Initialize RecyclerView
                val recyclerView = root.findViewById<RecyclerView>(R.id.event_recycler_view)
                recyclerView.layoutManager = LinearLayoutManager(root.context)

                // Initialize EventAdapter FIXME
                val adapter = EventAdapter(eventList) { event ->
                    // Handle delete button click
                    println("ServerEvent deleted: ${event.title}")
                }

                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
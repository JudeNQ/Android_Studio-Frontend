package com.example.eventcal.pages.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventcal.MainActivity
import com.example.eventcal.databinding.FragmentHomeBinding
import com.example.eventcal.pages.home.HomeViewModel
import com.example.eventcal.R
import com.example.eventcal.adapters.EventAdapter
import com.example.eventcal.models.Event
import com.example.eventcal.pojo.ServerEvent
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter



class SearchFragment : Fragment() {
    private var _binding : FragmentHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(SearchViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val mainActivity : MainActivity = activity as MainActivity

        //TODO Search Filters - currently identical to home page, add organization functionality

        var eventList = ArrayList<Event>()

        mainActivity.doGetEventList("10/11/2024") {
            if (it != null) {
                for(event: ServerEvent in it.data) {
                    eventList.add(
                        Event(
                            event.eventName,
                            LocalDateTime.parse(event.date, DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                            event.description
                        )
                    )
                }
                // Initialize RecyclerView
                val recyclerView = root.findViewById<RecyclerView>(R.id.event_recycler_view)
                recyclerView.layoutManager = LinearLayoutManager(root.context)
                // Initialize EventAdapter
                val adapter = EventAdapter(eventList) { event ->
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
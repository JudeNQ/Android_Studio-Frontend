package com.example.eventcal.pages.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventcal.MainActivity
import com.example.eventcal.databinding.FragmentSearchBinding
import com.example.eventcal.pages.home.HomeViewModel
import com.example.eventcal.R
import com.example.eventcal.adapters.EventAdapter
import com.example.eventcal.models.Event
import com.example.eventcal.pojo.ServerEvent
import com.google.android.material.tabs.TabLayout
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter



class SearchFragment : Fragment() {
    private var _binding : FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var eventAdapter: EventAdapter
    private var allEvents: List<Event> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val mainActivity : MainActivity = activity as MainActivity

        //TODO test Search Filters -apply changes to home page and make home page saved events only, add organization functionality
        //TODO also need to add a way to get to this search fragment from the home page. Probably from clicking the search icon, then back thru the back button
        //TODO Save button stuff

        // TODO Tab selection listener
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> { // If 'Events' tab is selected
                        setupEventRecyclerView()
                    }
                    1 -> { // If 'Organizations' tab is selected
                        setupOrganizationRecyclerView()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        //TODO need to fix this, not sure how orgs are stored but this should be similar to dogeteventlist below
        mainActivity.doGetOrganizationList { response ->
            if (response != null) {
                allOrganizations = response.data.map { serverOrganization ->
                    Organization(serverOrganization.name, serverOrganization.description)
                }
            }
        }


        mainActivity.doGetEventList("10/11/2024") { response ->
            if (response != null) {
                allEvents = response.data.map{serverEvent ->
                    Event(
                        serverEvent.eventName,
                        LocalDateTime.parse(serverEvent.date, DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        serverEvent.description
                    )
                }
                // Initialize RecyclerView
                val recyclerView = root.findViewById<RecyclerView>(R.id.event_recycler_view)
                recyclerView.layoutManager = LinearLayoutManager(root.context)

                // Initialize EventAdapter
                eventAdapter = EventAdapter(allEvents, showSavedOnly = false) { event -> }
                recyclerView.adapter = eventAdapter
            }
        }

        //TODO Implement Clicking the back button to return to the home fragment, not sure how to do it
        binding.backButton.setOnClickListener {
            // Code to return to home fragment
        }

        //Search bar filtering
        //TODO TEST ME!!!! :) Similar as in Home Frag? im not sure, sorry for spaghetti code
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?){}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int){
                val searchText = s.toString().lowercase()
                eventAdapter.filter(searchText)
                organizationAdapter.filter(searchText)
            }
        })

        //Save button Functionality
        //Todo  make text reflect if it is saved or not
        //      make the button save events



        return root
    }

    private fun setupEventRecyclerView() {
        // Initialize RecyclerView for events
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        eventAdapter = EventAdapter(allEvents, showSavedOnly = false) { event ->
        }
        recyclerView.adapter = eventAdapter
    }

    private fun setupOrganizationRecyclerView() {
        // Initialize RecyclerView for organizations
        /* Not sure how this will work yet, but this is a start
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        organizationAdapter = OrganizationAdapter(allOrganizations) { organization ->
            // Handle save button click for organizations if necessary
        }
        recyclerView.adapter = organizationAdapter

         */
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
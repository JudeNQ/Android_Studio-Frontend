package com.example.eventcal.pages.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        /*REMOVE AFTER TESTING
        val homeViewModel =
            ViewModelProvider(this).get(SearchViewModel::class.java)
*/
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val mainActivity : MainActivity = activity as MainActivity

        //TODO Search Filters - currently identical to home page, add organization functionality

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
                eventAdapter = EventAdapter(allEvents) { event ->
                    //TODO add delete and save functionality
                }
                recyclerView.adapter = eventAdapter
            }
        }
        //Search bar filtering
        //TODO TEST ME!!!! :)
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?){}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int){
                val searchText = s.toString().lowercase()
                eventAdapter.filter(searchText)
            }
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
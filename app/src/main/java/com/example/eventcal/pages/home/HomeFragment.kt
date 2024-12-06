package com.example.eventcal.pages.home

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventcal.MainActivity
import com.example.eventcal.R
import com.example.eventcal.adapters.EventAdapter
import com.example.eventcal.databinding.FragmentHomeBinding
import com.example.eventcal.models.Event
import com.example.eventcal.pages.search.SearchFragment
import com.example.eventcal.pojo.ServerEvent
import com.example.eventcal.userStorage.UserInfo
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment() {
    private var _binding : FragmentHomeBinding? = null
    private lateinit var eventAdapter: EventAdapter
    private val binding get() = _binding!!
    val mainActivity : MainActivity = activity as MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        //Create an empty list for the events to be added to home page
        var eventList = ArrayList<Event>()

        mainActivity.doGetEventList("10/11/2024") {
            if (it != null) {
                for (event: ServerEvent in it.data) {
                    eventList.add(
                        Event(
                            event.eventName,
                            LocalDateTime.parse(event.date, DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                            event.description,
                            event.eventId
                        )
                    )
                }
                // Initialize RecyclerView
                val recyclerView = root.findViewById<RecyclerView>(R.id.event_recycler_view)
                recyclerView.layoutManager = LinearLayoutManager(root.context)

                // Initialize EventAdapter
                eventAdapter = EventAdapter(requireContext(), eventList, showSavedOnly = true) { event -> }
                recyclerView.adapter = eventAdapter
                eventAdapter.notifyDataSetChanged()
            }

        }
        //TODO Implement Clicking the search button to open the search fragment, not sure how to do it
        binding.searchButton.setOnClickListener {
            // TODO Navigate to SearchFragment

        }

        //Search bar filtering
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?){}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int){
                val searchText = s.toString().lowercase()
                eventAdapter.filter(searchText)
            }
        })

        //TODO copy all of this stuff to frag_search

        //Handle the create button being clicked
        val button = root.findViewById<AppCompatImageButton>(com.example.eventcal.R.id.create_event_btn)

        button.setOnClickListener {
            val dialog = CreateEventDialogFragment()
            dialog.show(parentFragmentManager, "CreateEventDialog")
        }

        // Set up listener for CreateEventDialog
        parentFragmentManager.setFragmentResultListener("createEventRequest", this) { _, bundle ->
            val name = bundle.getString("name")
            val organization = bundle.getString("org")
            val description = bundle.getString("bio")
            val location = bundle.getString("location")
            val date = bundle.getString("date")
            val startTime = bundle.getString("start_time")
            val endTime = bundle.getString("end_time")
            onCreateDialogPositiveClick(name, organization, description, location, date, startTime, endTime)
        }
        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun onCreateDialogPositiveClick(eventName : String?, organization : String?, description : String?, location : String?, date : String?, startTime : String?, endTime : String?) {
        val event = ServerEvent(eventName, organization, description, location, date, startTime, endTime)
        //Attempt to create the event
        mainActivity.createEvent(event) {
            if(it == null) {
                Toast.makeText(
                    binding.root.context,
                    "Failed to create the event",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else {
                Toast.makeText(
                    binding.root.context,
                    "Event created successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
//Dialog Frag
internal class CreateEventDialogFragment : DialogFragment() {

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(R.color.green)
    }

    //Make the buttons and display
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //Create some fun little alerts
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_create_event, null)

        // Set up button click listeners
        val positiveButton = view.findViewById<Button>(R.id.positive_button)
        val negativeButton = view.findViewById<Button>(R.id.negative_button)

        positiveButton.setOnClickListener {
            // Capture the result you want to send back to EventFragment TODO
            val name = view?.findViewById<EditText>(R.id.event_name_input)?.text.toString()
            val organization = view?.findViewById<EditText>(R.id.event_org_input)?.text.toString()
            val description = view?.findViewById<EditText>(R.id.bio_input)?.text.toString()
            val location = view?.findViewById<EditText>(R.id.event_location_input)?.text.toString()
            val date = view?.findViewById<EditText>(R.id.event_date_input)?.text.toString()
            val startTime = view?.findViewById<EditText>(R.id.editTextTimeStart)?.text.toString()
            val endTime = view?.findViewById<EditText>(R.id.editTextTimeEnd)?.text.toString()

            /*
             val name = bundle.getString("name")
            val organization = bundle.getString("org")
            val description = bundle.getString("bio")
            val location = bundle.getString("location")
            val date = bundle.getString("date")
            val startTime = bundle.getString("start_time")
            val endTime = bundle.getString("end_time")
             */

            //make sure they actually put stuff in
            if(name == "" || organization == "" || description == ""|| location == ""|| date == ""|| startTime == ""|| endTime == "") {
                Toast.makeText(
                    view?.context,
                    "Please fill out all fields",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else {
                // Send the result
                parentFragmentManager.setFragmentResult("createEventRequest", Bundle().apply {
                    putString("name", name)
                    putString("organization", organization)
                    putString("description", description)
                    putString("location", location)
                    putString("date", date)
                    putString("startTime", startTime)
                    putString("endTime", endTime)
                })
                //Then dismiss
                dismiss()
            }


        }

        negativeButton.setOnClickListener {
            // Handle negative action here
            dismiss()
        }

        // Inflate and set the layout for the dialog.
        // Pass null as the parent view because it's going in the dialog layout.
        builder.setView(view)
        return builder.create()
    }
}


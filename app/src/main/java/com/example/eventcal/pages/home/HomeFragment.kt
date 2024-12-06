package com.example.eventcal.pages.home

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
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
import com.example.eventcal.pojo.ServerEvent
import com.example.eventcal.userStorage.UserInfo
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeFragment : Fragment() {
    private var _binding : FragmentHomeBinding? = null
    private lateinit var eventAdapter: EventAdapter
    private val binding get() = _binding!!
    private lateinit var mainActivity : MainActivity
    private lateinit var recyclerView : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mainActivity = activity as MainActivity

        // Initialize RecyclerView
        recyclerView = root.findViewById<RecyclerView>(R.id.event_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(root.context)

        loadRecycler()

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

    private fun loadRecycler() {
        //We want to update the user data's saved events
        UserInfo.getInstance().savedEvents.clear()
        mainActivity.GetUsersEvents(UserInfo.getInstance().userId) {
            if (it != null) {
                Log.d("Get User", "Succeeded")
                for (event: ServerEvent in it.data) {
                    UserInfo.getInstance().savedEvents.add(Event(
                        event.eventName,
                        LocalDateTime.parse(event.date, DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        event.bio,
                        event.eventId
                    ))
                }
            }
            else {
                Log.d("Get User", "Failed")
            }

        }


        //Create an empty list for the events to be added to home page
        var eventList = ArrayList<Event>()

        mainActivity.doGetEventList("10/11/2024") {
            if (it != null) {
                for (event: ServerEvent in it.data) {
                    eventList.add(
                        Event(
                            event.eventName,
                            LocalDateTime.parse(event.date, DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                            event.bio,
                            event.eventId
                        )
                    )
                }



                // Initialize EventAdapter
                eventAdapter = EventAdapter(requireContext(), eventList, showSavedOnly = true) { event -> }
                recyclerView.adapter = eventAdapter
                eventAdapter.notifyDataSetChanged()
            }

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onCreateDialogPositiveClick(eventName : String?, organization : String?, description : String?, location : String?, date : String?, startTime : String?, endTime : String?) {
        val event = ServerEvent(eventName, organization, startTime, endTime, date, location, description)
        Log.d("POJO Time", startTime.toString())
        Log.d("POJO Time", endTime.toString())
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
                //Update the recycler view
                loadRecycler()
            }
        }
    }
}
//Dialog Frag
internal class CreateEventDialogFragment : DialogFragment() {

    val date12Format : SimpleDateFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
    val date24Format : SimpleDateFormat = SimpleDateFormat("HH:mm")

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

        //We need to get the editText from the dialog just for these two
        val startTimeEdit : EditText = view.findViewById(R.id.editTextTimeStart)
        val endTimeEdit : EditText = view.findViewById(R.id.editTextTimeEnd)

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
            val startTime = startTimeEdit.text.toString()
            val endTime = endTimeEdit.text.toString()

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
                if (!startTime.matches("\\d{1,2}:\\d{2} [APap][Mm]".toRegex()) ||
                    !endTime.matches("\\d{1,2}:\\d{2} [APap][Mm]".toRegex())) {
                    Toast.makeText(context, "Invalid time format. Please use hh:mm AM/PM.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                // Send the result
                parentFragmentManager.setFragmentResult("createEventRequest", Bundle().apply {
                    putString("name", name)
                    putString("org", organization)
                    putString("bio", description)
                    putString("location", location)
                    putString("date", date)
                    //We also need to convert to military time (since thats what the database uses
                    putString("start_time", date24Format.format(date12Format.parse(startTime)))
                    putString("end_time", date24Format.format(date12Format.parse(endTime)))
                })
                //Then dismiss
                dismiss()
            }


        }

        //Make it so that if you unfocus the time boxes it attempts to auto format
        startTimeEdit.setOnFocusChangeListener(OnFocusChangeListener { v: View, hasFocus: Boolean ->
            //User clicked off the text box
            if (!hasFocus) {
                val formattedTime = formatTime(startTimeEdit.getText().toString())
                if (formattedTime.compareTo("") < 0) {
                    startTimeEdit.setText("")
                    //Make a toast and say its invalid
                    Toast.makeText(
                        context,
                        "Invalid Time Format. Please use HH:MM AM/PM",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else {
                    startTimeEdit.setText(formattedTime)
                }
            }
        })

        endTimeEdit.setOnFocusChangeListener(OnFocusChangeListener { v: View, hasFocus: Boolean ->
            //User clicked off the text box
            if (!hasFocus) {
                val formattedTime = formatTime(endTimeEdit.getText().toString())
                if (formattedTime.compareTo("") < 0) {
                    endTimeEdit.setText("")
                    //Make a toast and say its invalid
                    Toast.makeText(
                        context,
                        "Invalid Time Format. Please use HH:MM AM/PM",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else {
                    endTimeEdit.setText(formattedTime)
                }
            }
        })

        negativeButton.setOnClickListener {
            // Handle negative action here
            dismiss()
        }

        // Inflate and set the layout for the dialog.
        // Pass null as the parent view because it's going in the dialog layout.
        builder.setView(view)
        return builder.create()
    }

    private fun formatTime(input: String): String {
        var input: String? = input
        if (input == null || input.isEmpty()) return ""

        //Simple regex-based parsing
        input = input.uppercase(Locale.getDefault())
            .replace(" ", "") // Remove spaces and ensure uppercase
        if (input.matches("\\d{1,2}:\\d{2} [AP]M".toRegex())) {
            return input //Already properly formatted
        }

        if (input.matches("\\d{1,2}:\\d{2} [AP]".toRegex())) {
            return input + "M" //Add missing 'M'
        }

        if (input.matches("\\d{1,2}:\\d{2}[AP]M".toRegex())) {
            //Add a space
            val first = input.substring(0, input.length - 2)
            val last = input.substring(input.length - 2)
            return first + " " + last
        }

        if (input.matches("\\d{1,2}:\\d{2}[AP]".toRegex())) {
            //Add a space and the missing M
            val first = input.substring(0, input.length - 1)
            val last = input.substring(input.length - 1)
            return first + " " + last + "M"
        }

        if (input.matches("\\d{1,2}:\\d{2}".toRegex())) {
            //Assume user input is in 24-hour format
            try {
                val parts = input.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                var hour = parts[0].toInt()
                val minute = parts[1].toInt()
                val period = if ((hour >= 12)) "PM" else "AM"
                hour = if ((hour > 12)) hour - 12 else (if (hour == 0) 12 else hour)
                return String.format("%02d:%02d %s", hour, minute, period)
            } catch (e: NumberFormatException) {
                return input // Invalid input; return as-is
            }
        }

        //Return input unchanged if it doesn't match any pattern
        return ""
    }
}


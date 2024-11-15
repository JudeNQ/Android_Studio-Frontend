package com.example.eventcal.pages.calendar

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.eventcal.databinding.FragmentCalendarBinding
import com.example.eventcal.databinding.FragmentHomeBinding
import com.example.eventcal.pages.home.HomeViewModel
import java.text.DateFormatSymbols

class CalendarFragment : Fragment() {
    private var _binding : FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    //Variables
    private var currentYear: Int = 0
    private var currentMonth: Int = 0
    private var currentDay: Int = 0

    // HashMap to store events for specific dates
    private val calendarEvents: MutableMap<String, String> = HashMap()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val HomeViewModel = ViewModelProvider(this).get(CalendarViewModel::class.java)

        //Inflate fragment_calendar.xml layout using FragmentCalendarBinding
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //Setup calendarView and SelectedDay
        val calendarView = binding.calendarView
        val selectedDayTextView = binding.selectedDay
        val dayContent = binding.dayContent

        //set an on date change listener on the calendarView
        calendarView.setOnDateChangeListener {view, year, month, dayofMonth ->

            //Update current day variables
            currentYear = year
            currentMonth = month
            currentDay = dayofMonth

            val dateKey = "$currentYear-$currentMonth-$currentDay"
            val monthName = DateFormatSymbols().months[month]
            val selectedDate = "$monthName $dayofMonth"
            selectedDayTextView.text = selectedDate

            // Show the saved event for the selected date, if any
            if (calendarEvents.containsKey(dateKey)) {
                val event = calendarEvents[dateKey]
                binding.textInput.setText(event)
            } else {
                binding.textInput.setText("") // Clear the text if no event is saved for the date
            }

            //Handle visibilty
            if(dayContent.visibility == View.GONE){
                dayContent.visibility = View.VISIBLE
            }
        }

        //setup up the button and textInput
        val saveButton = binding.saveButton
        val textInput = binding.textInput

        //An OnClickListener to clear edit text
        saveButton.setOnClickListener{
            val text = textInput.text.toString()
            if (text.isNotEmpty()) {
                val dateKey = "$currentYear-$currentMonth-$currentDay"
                calendarEvents[dateKey] = text // Save the text to the map with the date as the key
                textInput.setText("") // Clear the text input after saving
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
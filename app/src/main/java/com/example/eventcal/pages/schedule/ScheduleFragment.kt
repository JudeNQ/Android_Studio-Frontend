package com.example.eventcal.pages.schedule

import android.app.AlertDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventcal.MainActivity
import com.example.eventcal.R
import com.example.eventcal.adapters.TimeAdapter
import com.example.eventcal.databinding.FragmentScheduleBinding
import com.example.eventcal.models.TimeData
import java.util.Calendar


class ScheduleFragment : Fragment() {
    private var _binding : FragmentScheduleBinding? = null

    private lateinit var mainActivity: MainActivity
    private lateinit var recyclerView: RecyclerView

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val scheduleViewModel =
            ViewModelProvider(this).get(ScheduleViewModel::class.java)

        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mainActivity = activity as MainActivity

        recyclerView = root.findViewById<RecyclerView>(com.example.eventcal.R.id.schedule_recycler_view)
        // Initialize RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(binding.root.context)

        //load existing schedules
        loadSchedules()

        //Handle the create button being clicked
        val button = root.findViewById<AppCompatImageButton>(com.example.eventcal.R.id.create_btn)

        button.setOnClickListener {
            //Create a little pop up to give them an option of making a group or joining one

            // Initializing the popup menu and giving the reference as current context
            val popupMenu: PopupMenu = PopupMenu(root.context, button)

            // Inflating popup menu from popup_menu.xml file
            popupMenu.getMenuInflater().inflate(com.example.eventcal.R.menu.schedule_popup, popupMenu.getMenu())
            popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(menuItem: MenuItem): Boolean {
                    when(menuItem.title) {
                        "Create Schedule" -> showCreateDialog()
                        //Display more of a popup to create a group
                    }

                    return true
                }
            })

            // Showing the popup menu
            popupMenu.show()
        }

        // Set up listener for CreateGroupDialog
        parentFragmentManager.setFragmentResultListener("createScheduleRequest", this) { _, bundle ->
            onCreateSchedulePositiveClick()
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Override the back button on the device
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            mainActivity.findNavController(R.id.nav_host_fragment_activity_main).navigate(R.id.navigation_group)
        }

        // Enable back arrow on the toolbar if you have one
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Navigate back to GroupFragment
                mainActivity.findNavController(R.id.nav_host_fragment_activity_main).navigate(R.id.navigation_group)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun loadSchedules() {
        //Load the users schedules
    }

    fun showCreateDialog() {
        //Create a dialog popup to make a schedule
        val dialog = CreateScheduleDialogFragment()
        dialog.show(parentFragmentManager, "CreateScheduleDialog")
    }

    fun onCreateSchedulePositiveClick() {
        //Create the schedule
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

internal class CreateScheduleDialogFragment : DialogFragment() {

    enum class weekDays {
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        SUNDAY
    }

    private var selectedDay: weekDays? = null
    private var startTime : String = ""
    private var endTime : String = ""

    /*
    private var mondayAdapter: TimeAdapter? = null
    private var tuesdayAdapter: TimeAdapter? = null
    private var wednesdayAdapter: TimeAdapter? = null
    private var thursdayAdapter: TimeAdapter? = null
    private var fridayAdapter: TimeAdapter? = null
    private var saturdayAdapter: TimeAdapter? = null
    private var sundayAdapter: TimeAdapter? = null

     */

    private var currentAdapter: TimeAdapter? = null

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(R.color.green)
    }

    //Make the buttons and display
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //Create some fun little alerts
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_createschedule, null)

        //Get the buttons for each day of the week:
        val mondayButton = view.findViewById<ImageButton>(R.id.add_more_button_mon)
        val tuesdayButton = view.findViewById<ImageButton>(R.id.add_more_button_tues)
        val wednesdayButton = view.findViewById<ImageButton>(R.id.add_more_button_wed)
        val thursdayButton = view.findViewById<ImageButton>(R.id.add_more_button_thurs)
        val fridayButton = view.findViewById<ImageButton>(R.id.add_more_button_fri)
        val saturdayButton = view.findViewById<ImageButton>(R.id.add_more_button_sat)
        val sundayButton = view.findViewById<ImageButton>(R.id.add_more_button_sun)

        // Get the recycler for each day of the week
        val mondayRecycler = view.findViewById<RecyclerView>(R.id.time_period_recycler_view_mon)
        val tuesdayRecycler = view.findViewById<RecyclerView>(R.id.time_period_recycler_view_tues)
        val wednesdayRecycler = view.findViewById<RecyclerView>(R.id.time_period_recycler_view_wed)
        val thursdayRecycler = view.findViewById<RecyclerView>(R.id.time_period_recycler_view_thurs)
        val fridayRecycler = view.findViewById<RecyclerView>(R.id.time_period_recycler_view_fri)
        val saturdayRecycler = view.findViewById<RecyclerView>(R.id.time_period_recycler_view_sat)
        val sundayRecycler = view.findViewById<RecyclerView>(R.id.time_period_recycler_view_sun)

        //Set up layout managers for each RecyclerView (This is real gross)
        val mondayLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val tuesdayLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val wednesdayLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val thursdayLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val fridayLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val saturdayLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val sundayLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        //Set individual layout managers to the corresponding RecyclerViews
        mondayRecycler.layoutManager = mondayLayoutManager
        tuesdayRecycler.layoutManager = tuesdayLayoutManager
        wednesdayRecycler.layoutManager = wednesdayLayoutManager
        thursdayRecycler.layoutManager = thursdayLayoutManager
        fridayRecycler.layoutManager = fridayLayoutManager
        saturdayRecycler.layoutManager = saturdayLayoutManager
        sundayRecycler.layoutManager = sundayLayoutManager

        // Set up adapters for each day of the week
        val mondayAdapter = TimeAdapter(ArrayList<TimeData>()) { timeData ->
            // Handle time picker
        }
        val tuesdayAdapter = TimeAdapter(ArrayList<TimeData>()) { timeData ->
            // Handle time picker
        }
        val wednesdayAdapter = TimeAdapter(ArrayList<TimeData>()) { timeData ->
            // Handle time picker
        }
        val thursdayAdapter = TimeAdapter(ArrayList<TimeData>()) { timeData ->
            // Handle time picker
        }
        val fridayAdapter = TimeAdapter(ArrayList<TimeData>()) { timeData ->
            // Handle time picker
        }
        val saturdayAdapter = TimeAdapter(ArrayList<TimeData>()) { timeData ->
            // Handle time picker
        }
        val sundayAdapter = TimeAdapter(ArrayList<TimeData>()) { timeData ->
            // Handle time picker
        }

        //Set the adapters to their corresponding RecyclerViews
        mondayRecycler.adapter = mondayAdapter
        tuesdayRecycler.adapter = tuesdayAdapter
        wednesdayRecycler.adapter = wednesdayAdapter
        thursdayRecycler.adapter = thursdayAdapter
        fridayRecycler.adapter = fridayAdapter
        saturdayRecycler.adapter = saturdayAdapter
        sundayRecycler.adapter = sundayAdapter

        //Set up button listeners for each day
        mondayButton.setOnClickListener {
            selectedDay = weekDays.MONDAY
            currentAdapter = mondayAdapter
            val dialog = TimePickerFragment()
            dialog.setTargetFragment(this, 0)
            dialog.show(parentFragmentManager, "TimePickerDialog")
        }

        tuesdayButton.setOnClickListener {
            selectedDay = weekDays.TUESDAY
            currentAdapter = tuesdayAdapter
            val dialog = TimePickerFragment()
            dialog.setTargetFragment(this, 0)
            dialog.show(parentFragmentManager, "TimePickerDialog")
        }

        wednesdayButton.setOnClickListener {
            selectedDay = weekDays.WEDNESDAY
            currentAdapter = wednesdayAdapter
            val dialog = TimePickerFragment()
            dialog.setTargetFragment(this, 0)
            dialog.show(parentFragmentManager, "TimePickerDialog")
        }

        thursdayButton.setOnClickListener {
            selectedDay = weekDays.THURSDAY
            currentAdapter = thursdayAdapter
            val dialog = TimePickerFragment()
            dialog.setTargetFragment(this, 0)
            dialog.show(parentFragmentManager, "TimePickerDialog")
        }

        fridayButton.setOnClickListener {
            selectedDay = weekDays.FRIDAY
            currentAdapter = fridayAdapter
            val dialog = TimePickerFragment()
            dialog.setTargetFragment(this, 0)
            dialog.show(parentFragmentManager, "TimePickerDialog")
        }

        saturdayButton.setOnClickListener {
            selectedDay = weekDays.SATURDAY
            currentAdapter = saturdayAdapter
            val dialog = TimePickerFragment()
            dialog.setTargetFragment(this, 0)
            dialog.show(parentFragmentManager, "TimePickerDialog")
        }

        sundayButton.setOnClickListener {
            selectedDay = weekDays.SUNDAY
            currentAdapter = sundayAdapter
            val dialog = TimePickerFragment()
            dialog.setTargetFragment(this, 0)
            dialog.show(parentFragmentManager, "TimePickerDialog")
        }

        // Set up button click listeners
        val positiveButton = view.findViewById<Button>(R.id.positive_button)
        val negativeButton = view.findViewById<Button>(R.id.negative_button)

        positiveButton.setOnClickListener {
            // Capture the result you want to send back to GroupFragment
            //Actually suffer and find some way to do this
            parentFragmentManager.setFragmentResult("createScheduleRequest", Bundle().apply {
            })
            //Then dismiss
            dismiss()
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

    // Method to handle the time picked from TimePickerFragment
    fun onTimePicked(hourOfDay: Int, minute: Int) {
        // Do something with the time
        if(startTime == "") {
            startTime = "$hourOfDay:$minute"
            val dialog = TimePickerFragment()
            dialog.setTargetFragment(this, 0)
            dialog.show(parentFragmentManager, "TimePickerDialog")
        }
        else {
            endTime = "$hourOfDay:$minute"
            checkTime();
        }
    }

    fun checkTime() {
        if(startTime != "" && endTime != "") {
            //Update the time for a thing.
            //Toast.makeText(context, "Time set: " + startTime + " - " + endTime, Toast.LENGTH_SHORT).show()
            //Make the time data object (to add to the recycler view
            val timeData = TimeData(startTime, endTime)
            currentAdapter?.addTimeData(timeData)
            Toast.makeText(context, "Time added " + currentAdapter?.itemCount.toString(), Toast.LENGTH_SHORT).show()

            //Reset everything so we're good for the next one
            startTime = ""
            endTime = ""
            currentAdapter = null;
        }
    }
}

internal class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker.
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it.
        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        //Send the selected time back to the target fragment
        val targetFragment = targetFragment
        if (targetFragment is CreateScheduleDialogFragment) {
            targetFragment.onTimePicked(hourOfDay, minute)
        }
    }
}
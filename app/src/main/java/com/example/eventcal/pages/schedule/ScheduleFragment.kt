package com.example.eventcal.pages.schedule

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
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
import com.example.eventcal.models.WeekDays
import com.example.eventcal.pojo.CreateSchedule
import com.example.eventcal.pojo.Schedule
import com.example.eventcal.userStorage.UserInfo

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

        // Set up listener for CreateScheduleDialog
        parentFragmentManager.setFragmentResultListener("createScheduleRequest", viewLifecycleOwner) { _, bundle ->
            @Suppress("UNCHECKED_CAST")
            val scheduleMap = bundle.getSerializable("scheduleMap") as? HashMap<WeekDays, ArrayList<Pair<String, String>>>
            if (scheduleMap != null) {
                // Use the scheduleMap as needed
                Toast.makeText(context, "Schedule created successfully!", Toast.LENGTH_SHORT).show()
                uploadSchedule(scheduleMap)
                loadSchedules()
            }
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
        //Get schedules from the server and populate the recycler view
    }

    fun uploadSchedule(scheduleMap: Map<WeekDays, List<Pair<String, String>>>) {
        //Upload the schedule to the server
        val schedule = Schedule(
            convertToAndroidPair(scheduleMap[WeekDays.MONDAY]),
            convertToAndroidPair(scheduleMap[WeekDays.TUESDAY]),
            convertToAndroidPair(scheduleMap[WeekDays.WEDNESDAY]),
            convertToAndroidPair(scheduleMap[WeekDays.THURSDAY]),
            convertToAndroidPair(scheduleMap[WeekDays.FRIDAY]),
            convertToAndroidPair(scheduleMap[WeekDays.SATURDAY]),
            convertToAndroidPair(scheduleMap[WeekDays.SUNDAY])
        )

        mainActivity.createSchedule(CreateSchedule(schedule, UserInfo.getInstance().userId)) {
            if(it == null) {
                Toast.makeText(context, "Error uploading schedule", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(context, "Schedule uploaded successfully!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Kotlin vs android pairs is wack
    fun convertToAndroidPair(list: List<Pair<String, String>>?): List<android.util.Pair<String, String>>? {
        return list?.map { android.util.Pair(it.first, it.second) }
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

    var adapterList : ArrayList<TimeAdapter> = ArrayList<TimeAdapter>()

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

        // Set up adapters for each day of the week
        val mondayAdapter = TimeAdapter(ArrayList<TimeData>(), WeekDays.MONDAY)
        val tuesdayAdapter = TimeAdapter(ArrayList<TimeData>(), WeekDays.TUESDAY)
        val wednesdayAdapter = TimeAdapter(ArrayList<TimeData>(), WeekDays.WEDNESDAY)
        val thursdayAdapter = TimeAdapter(ArrayList<TimeData>(), WeekDays.THURSDAY)
        val fridayAdapter = TimeAdapter(ArrayList<TimeData>(), WeekDays.FRIDAY)
        val saturdayAdapter = TimeAdapter(ArrayList<TimeData>(), WeekDays.SATURDAY)
        val sundayAdapter = TimeAdapter(ArrayList<TimeData>(), WeekDays.SUNDAY)

        adapterList.add(mondayAdapter)
        adapterList.add(tuesdayAdapter)
        adapterList.add(wednesdayAdapter)
        adapterList.add(thursdayAdapter)
        adapterList.add(fridayAdapter)
        adapterList.add(saturdayAdapter)
        adapterList.add(sundayAdapter)

        //Set the adapters to their corresponding RecyclerViews
        mondayRecycler.adapter = mondayAdapter
        mondayRecycler.layoutManager = LinearLayoutManager(context)
        mondayRecycler.setNestedScrollingEnabled(true);

        //Manually set the height so it doesn't crash after deleting something
        tuesdayRecycler.adapter = tuesdayAdapter
        tuesdayRecycler.layoutManager = LinearLayoutManager(context)
        tuesdayRecycler.setNestedScrollingEnabled(true);

        wednesdayRecycler.adapter = wednesdayAdapter
        wednesdayRecycler.layoutManager = LinearLayoutManager(context)
        wednesdayRecycler.setNestedScrollingEnabled(true);

        thursdayRecycler.adapter = thursdayAdapter
        thursdayRecycler.layoutManager = LinearLayoutManager(context)
        thursdayRecycler.setNestedScrollingEnabled(true);

        fridayRecycler.adapter = fridayAdapter
        fridayRecycler.layoutManager = LinearLayoutManager(context)
        fridayRecycler.setNestedScrollingEnabled(true);

        saturdayRecycler.adapter = saturdayAdapter
        saturdayRecycler.layoutManager = LinearLayoutManager(context)
        saturdayRecycler.setNestedScrollingEnabled(true);

        sundayRecycler.adapter = sundayAdapter
        sundayRecycler.layoutManager = LinearLayoutManager(context)
        sundayRecycler.setNestedScrollingEnabled(true);

        //Set up button listeners for each day
        mondayButton.setOnClickListener {
            //Add another item to the adapter
            mondayAdapter.addTimeData()
        }

        tuesdayButton.setOnClickListener {
            tuesdayAdapter.addTimeData()
        }

        wednesdayButton.setOnClickListener {
            wednesdayAdapter.addTimeData()
        }

        thursdayButton.setOnClickListener {
            thursdayAdapter.addTimeData()
        }

        fridayButton.setOnClickListener {
            fridayAdapter.addTimeData()
        }

        saturdayButton.setOnClickListener {
            saturdayAdapter.addTimeData()
        }

        sundayButton.setOnClickListener {
            sundayAdapter.addTimeData()
        }

        // Set up button click listeners
        val positiveButton = view.findViewById<Button>(R.id.positive_button)
        val negativeButton = view.findViewById<Button>(R.id.negative_button)

        positiveButton.setOnClickListener {
            // Capture the result you want to send back to GroupFragment
            //We need to get the time data from each recycler and add it to some list to upload to the data base
            //First lets make sure the items are formatted properly
            val scheduleMap = mutableMapOf<WeekDays, ArrayList<Pair<String, String>>>()
            //Populate the map first
            for(weekDay in WeekDays.entries) {
                scheduleMap[weekDay] = ArrayList<Pair<String, String>>()
            }
            var isGood : Boolean = true
            //Loop through all adapters
            for (adapter in adapterList) {
                for (item in adapter.getTimeDataList()) {
                    //If one item is invalid, we want to stop
                    if (item.startTime.isBlank() || item.endTime.isBlank()) {
                        isGood = false
                        break
                    }
                    //Otherwise add it to the map
                    else {
                        //Insert the pair into the map
                        if(!scheduleMap.containsKey(adapter.day)) {
                            scheduleMap[adapter.day] = ArrayList<Pair<String, String>>()
                        }
                        scheduleMap[adapter.day]?.add(Pair(item.startTime, item.endTime))
                    }
                }
                //fallthrough
                if (!isGood) break
            }

            //We messed up somewhere
            if (!isGood) {
                Toast.makeText(
                    context,
                    "Invalid Format. Make sure all of your times are formatted correctly xx:xxAM/PM",
                    Toast.LENGTH_SHORT
                ).show()
            }
            //We want to send over the map to the fragment
            else {
                //Use setFragmentResult to send the data back
                val bundle = Bundle().apply {
                    putSerializable("scheduleMap", HashMap(scheduleMap))
                }
                parentFragmentManager.setFragmentResult("createScheduleRequest", bundle)
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
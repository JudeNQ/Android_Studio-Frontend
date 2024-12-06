package com.example.eventcal.pages.groups

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventcal.MainActivity
import com.example.eventcal.R
import com.example.eventcal.adapters.GroupAdapter
import com.example.eventcal.adapters.TimeAdapter
import com.example.eventcal.databinding.FragmentGroupBinding
import com.example.eventcal.models.Group
import com.example.eventcal.models.TimeData
import com.example.eventcal.models.WeekDays
import com.example.eventcal.pojo.CreateSchedule
import com.example.eventcal.pojo.JoinGroup
import com.example.eventcal.pojo.Schedule
import com.example.eventcal.pojo.ServerGroup
import com.example.eventcal.userStorage.UserInfo
import java.text.SimpleDateFormat
import java.util.Locale


class GroupFragment : Fragment(), GroupAdapter.GroupActionListener {
    private var _binding : FragmentGroupBinding? = null

    private val binding get() = _binding!!

    private var groupId : String = "";
    lateinit var mainActivity : MainActivity
    lateinit var recyclerView : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val groupViewModel =
            ViewModelProvider(this).get(GroupViewModel::class.java)

        _binding = FragmentGroupBinding.inflate(inflater, container, false)
        val root: View = binding.root
        mainActivity = activity as MainActivity

        recyclerView = root.findViewById<RecyclerView>(com.example.eventcal.R.id.group_recycler_view)
        // Initialize RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(binding.root.context)

        loadGroups()

        //Handle the create button being clicked
        val button = root.findViewById<AppCompatImageButton>(com.example.eventcal.R.id.create_btn)

        button.setOnClickListener {
            //Create a little pop up to give them an option of making a group or joining one

            // Initializing the popup menu and giving the reference as current context
            val popupMenu: PopupMenu = PopupMenu(root.context, button)

            // Inflating popup menu from popup_menu.xml file
            popupMenu.getMenuInflater().inflate(com.example.eventcal.R.menu.group_popup, popupMenu.getMenu())
            popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(menuItem: MenuItem): Boolean {
                    when(menuItem.title) {
                        "Create Group" -> showCreateDialog()
                            //Display more of a popup to create a group
                        "Add Schedule" -> showAddScheduleDialog()
                            //Display more of a popup to join a group
                    }

                    return true
                }
            })

            // Showing the popup menu
            popupMenu.show()
        }

        // Set up listener for CreateGroupDialog
        parentFragmentManager.setFragmentResultListener("createGroupRequest", this) { _, bundle ->
            val name = bundle.getString("name")
            val password = bundle.getString("password")
            val bio = bundle.getString("bio")
            onCreateDialogPositiveClick(name, password, bio)
        }

        // Set up listener for JoinGroupDialog
        parentFragmentManager.setFragmentResultListener("joinGroupRequest", this) { _, bundle ->
            val password = bundle.getString("password")
            onJoinDialogPositiveClick(password)
        }

        // Set up listener for CreateScheduleDialog
        parentFragmentManager.setFragmentResultListener("createScheduleRequest", viewLifecycleOwner) { _, bundle ->
            @Suppress("UNCHECKED_CAST")
            val scheduleMap = bundle.getSerializable("scheduleMap") as? HashMap<WeekDays, ArrayList<Pair<String, String>>>
            if (scheduleMap != null) {
                // Use the scheduleMap as needed
                Toast.makeText(context, "Schedule created successfully!", Toast.LENGTH_SHORT).show()
                uploadSchedule(scheduleMap)
            }
        }

        return root
    }

    private fun loadGroups() {
        //Create an empty list for the groups to be added to group page
        var groupList = ArrayList<Group>()

        //Get the logged in users list of groups
        mainActivity.doGetGroupList() {
            //Initialize an empty group
            if (it != null) {
                var pendingRequests = 0

                for (group: ServerGroup in it.data) {
                    val currGroup = Group(
                        name = group.groupName,
                        bio = group.bio,
                        leader = group.leader,
                        members = group.members,
                        id = group.groupId,
                        schedule = null
                    )

                    //Now we also check to see if the user is apart of this group
                    for(member in group.members) {
                        if (member == UserInfo.getInstance().userId) {
                            //If its a member of a group, add to group list (if not already there)
                            if(!UserInfo.getInstance().groups.contains(currGroup.id)) {
                                UserInfo.getInstance().groups.add(currGroup.id)
                            }

                            pendingRequests++;

                            //Also get the schedule from the server (we only do this if the user is apart of this group
                            //For reasons ig
                            mainActivity.getGroupSchedule(currGroup.id) { schedule ->
                                if (schedule != null) {
                                    currGroup.schedule = schedule
                                    Log.d(
                                        "Group Adapter Schedule",
                                        "The schedule wasn't null in fragment"
                                    )
                                }
                                else {
                                    Log.d(
                                        "Group Adapter Schedule",
                                        "The schedule was null in fragment"
                                    )
                                }
                                pendingRequests--
                                // Update UI once all requests are complete
                                if (pendingRequests == 0) {
                                    val adapter = GroupAdapter(groupList, this)
                                    recyclerView.adapter = adapter
                                    adapter.notifyDataSetChanged()
                                }
                            }
                        }
                    }

                    groupList.add(currGroup)
                }

                val adapter = GroupAdapter(groupList, this)

                //If there are no pending requests, update the UI immediately
                if (pendingRequests == 0) {
                    val adapter = GroupAdapter(groupList, this)
                    recyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onJoinGroup(groupId: String?) {
        val dialog = JoinGroupDialogFragment()
        dialog.show(parentFragmentManager, "JoinGroupDialog")
        if (groupId != null) {
            this.groupId = groupId
        };
    }

    fun onJoinDialogPositiveClick(pass : String?) {
        //Try to join the group
        //Make the groupJSON file
        val group = JoinGroup(UserInfo.getInstance().userId, groupId, pass)
        //Send the request
        mainActivity.JoinGroup(group) {
            if(it == null) {
                Toast.makeText(
                    binding.root.context,
                    "Failed to join the group",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else {
                if(it.message == "Joining group was successful") {
                    Toast.makeText(
                        binding.root.context,
                        "Joined group successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    //The user has joined the group. Update the display?
                    UserInfo.getInstance().groups.add(groupId)

                    //Update the recycler view (how idk)
                    loadGroups()
                }
                else {
                    Toast.makeText(
                        binding.root.context,
                        "Incorrect Password",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }
    }

    fun onCreateDialogPositiveClick(groupName : String?, groupPassword : String?, groupBio : String?) {
        val group = ServerGroup(groupName, groupBio, groupPassword)
        group.leader = UserInfo.getInstance().userId

        //Attempt to create the group
        mainActivity.createGroup(group) {
            if(it == null) {
                Toast.makeText(
                    binding.root.context,
                    "Failed to create the group",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else {
                Toast.makeText(
                    binding.root.context,
                    "Group created successfully",
                    Toast.LENGTH_SHORT
                ).show()
                //Update the UserInfo to add this group to it
                UserInfo.getInstance().groups.add(it.groupId)
                loadGroups()
            }
        }


    }

    fun showCreateDialog() {
        val dialog = CreateGroupDialogFragment()
        dialog.show(parentFragmentManager, "CreateGroupDialog")
    }

    fun showAddScheduleDialog() {
        val dialog = CreateScheduleDialogFragment()
        dialog.show(parentFragmentManager, "CreateScheduleDialog")
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
}

internal class JoinGroupDialogFragment : DialogFragment() {
    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(R.color.green)
    }

    //Make the buttons and display
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //Create some fun little alerts
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_joingroup, null)

        // Set up button click listeners
        val positiveButton = view.findViewById<Button>(R.id.positive_button)
        val negativeButton = view.findViewById<Button>(R.id.negative_button)

        positiveButton.setOnClickListener {
            // Capture the result you want to send back to GroupFragment
            val password = view?.findViewById<EditText>(R.id.password_input)?.text.toString()

            //make sure they actually put stuff in
            if(password == "") {
                Toast.makeText(
                    view?.context,
                    "Please fill out all fields",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else {
                // Send the result
                parentFragmentManager.setFragmentResult("joinGroupRequest", Bundle().apply {
                    putString("password", password)
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

internal class CreateGroupDialogFragment : DialogFragment() {

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(R.color.green)
    }

    //Make the buttons and display
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //Create some fun little alerts
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_creategroup, null)

        // Set up button click listeners
        val positiveButton = view.findViewById<Button>(R.id.positive_button)
        val negativeButton = view.findViewById<Button>(R.id.negative_button)

        positiveButton.setOnClickListener {
            // Capture the result you want to send back to GroupFragment
            val name = view?.findViewById<EditText>(R.id.name_input)?.text.toString()
            val password = view?.findViewById<EditText>(R.id.password_input)?.text.toString()
            val bio = view?.findViewById<EditText>(R.id.bio_input)?.text.toString()

            //make sure they actually put stuff in
            if(name == "" || password == "" || bio == "") {
                Toast.makeText(
                    view?.context,
                    "Please fill out all fields",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else {
                // Send the result
                parentFragmentManager.setFragmentResult("createGroupRequest", Bundle().apply {
                    putString("name", name)
                    putString("password", password)
                    putString("bio", bio)
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

internal class CreateScheduleDialogFragment : DialogFragment() {

    var adapterList : ArrayList<TimeAdapter> = ArrayList<TimeAdapter>()
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
                        val start : String = date24Format.format(date12Format.parse(item.startTime))
                        val end : String = date24Format.format(date12Format.parse(item.endTime))
                        Log.d("Group Adapter Schedule", "Start: $start End: $end")
                        scheduleMap[adapter.day]?.add(Pair(start, end))
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


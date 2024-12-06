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


    fun onCreateSchedulePositiveClick() {
        //Create the schedule
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
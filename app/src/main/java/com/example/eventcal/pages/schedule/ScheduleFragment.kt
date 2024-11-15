package com.example.eventcal.pages.schedule

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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
import com.example.eventcal.databinding.FragmentScheduleBinding
import com.example.eventcal.pages.home.HomeViewModel
import com.example.eventcal.pages.profile.ProfileViewModel

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
        parentFragmentManager.setFragmentResultListener("createGroupRequest", this) { _, bundle ->
            val name = bundle.getString("name")
            val password = bundle.getString("password")
            val bio = bundle.getString("bio")
            onCreateSchedulePositiveClick(name, password, bio)
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
    }

    fun onCreateSchedulePositiveClick(name : String?, password : String?, bio : String?) {
        //Create the schedule
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

internal class CreateScheduleDialogFragment : DialogFragment() {

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
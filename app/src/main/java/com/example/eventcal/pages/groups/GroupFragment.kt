package com.example.eventcal.pages.groups

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.collection.emptyLongSet
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventcal.MainActivity
import com.example.eventcal.R
import com.example.eventcal.adapters.GroupAdapter
import com.example.eventcal.databinding.FragmentGroupBinding
import com.example.eventcal.models.Group
import com.example.eventcal.pages.schedule.ScheduleFragment
import com.example.eventcal.pojo.CreateGroup
import com.example.eventcal.pojo.JoinGroup
import com.example.eventcal.pojo.ServerGroup
import com.example.eventcal.userStorage.UserInfo


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

        return root
    }

    private fun loadGroups() {
        //Create an empty list for the groups to be added to group page
        var groupList = ArrayList<Group>()

        //Get the logged in users list of groups
        mainActivity.doGetGroupList() {
            if (it != null) {
                for (group: ServerGroup in it.data) {
                    groupList.add(
                        Group(
                            group.groupName,
                            group.bio,
                            group.leader,
                            group.members,
                            group.groupId,
                        )
                    )
                }

                //Actually see if the user is apart of any of the groups
                for(group in groupList) {
                    for(member in group.members) {
                        if (member == UserInfo.getInstance().userId) {
                            //If its a member of a group, add to group list (if not already there)
                            if(!UserInfo.getInstance().groups.contains(group.id)) {
                                UserInfo.getInstance().groups.add(group.id)
                            }
                        }
                    }
                }

                val adapter = GroupAdapter(groupList, this)

                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
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
                //The user has joined the group. Update the display?
                UserInfo.getInstance().groups.add(groupId)

                //Update the recycler view (how idk)
                loadGroups()
            }
        }
    }

    fun onCreateDialogPositiveClick(groupName : String?, groupPassword : String?, groupBio : String?) {
        val group = ServerGroup(groupName, groupBio, groupBio)
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
        val navController = mainActivity.findNavController(R.id.nav_host_fragment_activity_main)
        navController.navigate(R.id.action_navigation_group_to_scheduleFragment)
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


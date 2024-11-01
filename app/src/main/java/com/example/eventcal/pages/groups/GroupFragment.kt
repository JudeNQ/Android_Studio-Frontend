package com.example.eventcal.pages.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventcal.MainActivity
import com.example.eventcal.adapters.GroupAdapter
import com.example.eventcal.databinding.FragmentGroupBinding
import com.example.eventcal.models.Group
import com.example.eventcal.pojo.ServerGroup
import com.example.eventcal.userStorage.UserInfo


class GroupFragment : Fragment() {
    private var _binding : FragmentGroupBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val groupViewModel =
            ViewModelProvider(this).get(GroupViewModel::class.java)

        _binding = FragmentGroupBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val mainActivity : MainActivity = activity as MainActivity

        //Create an empty list for the groups to be added to group page
        var groupList = ArrayList<Group>()

        //Get the logged in users list of groups
        mainActivity.doGetGroupList(UserInfo.info.userId) {
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

                // Initialize RecyclerView
                val recyclerView = root.findViewById<RecyclerView>(com.example.eventcal.R.id.group_recycler_view)
                recyclerView.layoutManager = LinearLayoutManager(root.context)

                val adapter = GroupAdapter(groupList)

                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        }

        //Handle the create button being clicked
        val button = root.findViewById<Button>(com.example.eventcal.R.id.create_btn)

        button.setOnClickListener {
            //Create a little pop up to give them an option of making a group or joining one

            // Initializing the popup menu and giving the reference as current context
            val popupMenu: PopupMenu = PopupMenu(root.context, button)

            // Inflating popup menu from popup_menu.xml file
            popupMenu.getMenuInflater().inflate(com.example.eventcal.R.menu.group_popup, popupMenu.getMenu())
            popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(menuItem: MenuItem): Boolean {
                    when(menuItem.title) {
                        "Create Group" -> Toast.makeText(
                            root.context,
                            "You Clicked to make a group YIPPEE",
                            Toast.LENGTH_SHORT
                        ).show()
                            //Display more of a popup to create a group
                        "Join Group" -> Toast.makeText(
                            root.context,
                            "You Clicked to make a join a group BOOOOOOOO",
                            Toast.LENGTH_SHORT
                        ).show()
                            //Display more of a popup to join a group
                    }

                    return true
                }
            })

            // Showing the popup menu
            popupMenu.show()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    public fun attemptLogin(groupId : String) {
        //Open up a fragment and AHHHHHHHHHHHHHHHHHHHHHHHHH
    }
}
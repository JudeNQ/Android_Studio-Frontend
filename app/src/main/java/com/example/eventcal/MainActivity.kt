package com.example.eventcal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import com.example.eventcal.pojo.CreateUser
import com.example.eventcal.pojo.ServerEvent
import com.example.eventcal.pojo.EventList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.eventcal.databinding.ActivityMainBinding
import com.example.eventcal.pojo.CreateGroup
import com.example.eventcal.pojo.GroupList
import com.example.eventcal.pojo.JoinGroup
import com.example.eventcal.pojo.ServerGroup


class MainActivity : AppCompatActivity() {

    private lateinit var apiInterface: APIInterface
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()

        apiInterface = APIClient.getClient().create(APIInterface::class.java)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        //val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val navController: NavController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_group,
                R.id.navigation_calendar,
                R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //Set up nav bar so the schedule screen works properly >:(
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_group -> {
                    //When selecting the group tab, pop ScheduleFragment if it’s on the back stack
                    if (navController.currentDestination?.id == R.id.scheduleFragment) {
                        navController.popBackStack(R.id.navigation_group, false)
                    }
                    navController.navigate(R.id.navigation_group)
                    true
                }

                R.id.navigation_home -> {
                    navController.navigate(R.id.navigation_home)
                    true
                }

                R.id.navigation_calendar -> {
                    navController.navigate(R.id.navigation_calendar)
                    true
                }

                R.id.navigation_profile -> {
                    navController.navigate(R.id.navigation_profile)
                    true
                }

                else -> false
            }
        }

    }

    //Attempts to create a user with the given userInfo.
    //Kind of returns (does something weird with calling "it" after)
    //the Userinfo result from server, so can compare
    //to make sure the attempt succeeded.
    private fun createUser(userInfo : CreateUser, onResult : (CreateUser?) -> Unit) {
        var createUser : CreateUser = userInfo
        val call = apiInterface.createUser(createUser)
        call.enqueue(object : Callback<CreateUser> {
            override fun onResponse(call: Call<CreateUser>, response: Response<CreateUser>) {
                val user1 = response.body()
                //Check to see the returned User Data is valid?
                onResult(user1)
            }

            override fun onFailure(call: Call<CreateUser>, t: Throwable) {
                onResult(null)
            }
        })
    }

    private fun createEvent(serverEventInfo : ServerEvent, onResult : (ServerEvent?) -> Unit) {
        var createServerEvent : ServerEvent = serverEventInfo
        val call = apiInterface.createEvent(createServerEvent)
        call.enqueue(object : Callback<ServerEvent> {
            override fun onResponse(call: Call<ServerEvent>, response: Response<ServerEvent>) {
                val event = response.body()
                //Check to see the returned User Data is valid?
                onResult(event)
            }

            override fun onFailure(call: Call<ServerEvent>, t: Throwable) {
                onResult(null)
            }
        })
    }

    public fun createGroup(groupInfo : ServerGroup, onResult : (ServerGroup?) -> Unit) {
        var createGroup : ServerGroup = groupInfo
        val call = apiInterface.createGroup(createGroup)
        call.enqueue(object : Callback<ServerGroup> {
            override fun onResponse(call: Call<ServerGroup>, response: Response<ServerGroup>) {
                val event = response.body()
                //Check to see the returned User Data is valid?
                onResult(event)
            }

            override fun onFailure(call: Call<ServerGroup>, t: Throwable) {
                onResult(null)
            }
        })
    }

    public fun JoinGroup(groupInfo : JoinGroup, onResult : (JoinGroup?) -> Unit) {
        var joinInfo : JoinGroup = groupInfo
        val call = apiInterface.joinGroup(joinInfo)
        call.enqueue(object : Callback<JoinGroup> {
            override fun onResponse(call: Call<JoinGroup>, response: Response<JoinGroup>) {
                val event = response.body()
                onResult(event)
            }

            override fun onFailure(call: Call<JoinGroup>, t: Throwable) {
                onResult(null)
            }
        })
    }

    //Enter date in MM/DD/YYYY format please :pray:
    public fun doGetEventList(date : String, onResult : (EventList?) -> Unit) {
        val call: Call<EventList> = apiInterface.doGetEventList(date)
        call.enqueue(object : Callback<EventList> {
            override fun onResponse(call: Call<EventList>, response: Response<EventList>) {
                val eventL =response.body()
                onResult(eventL)
            }

            override fun onFailure(call: Call<EventList>, t: Throwable) {
                onResult(null)
            }
        })
    }

    public fun doGetGroupList(userId : String, onResult : (GroupList?) -> Unit) {
        val call: Call<GroupList> = apiInterface.doGetUsersGroups(userId)
        call.enqueue(object : Callback<GroupList> {
            override fun onResponse(call: Call<GroupList>, response: Response<GroupList>) {
                val eventL =response.body()
                onResult(eventL)
            }

            override fun onFailure(call: Call<GroupList>, t: Throwable) {
                onResult(null)
            }
        })
    }

    public fun doGetGroupList(onResult : (GroupList?) -> Unit) {
        val call: Call<GroupList> = apiInterface.doGetAllGroups()
        call.enqueue(object : Callback<GroupList> {
            override fun onResponse(call: Call<GroupList>, response: Response<GroupList>) {
                val eventL =response.body()
                onResult(eventL)
            }

            override fun onFailure(call: Call<GroupList>, t: Throwable) {
                onResult(null)
            }
        })
    }
}

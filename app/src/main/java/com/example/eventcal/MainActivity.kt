package com.example.eventcal

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.eventcal.pojo.CreateUser
import com.example.eventcal.pojo.Event
import com.example.eventcal.pojo.EventList
import com.example.eventcal.pojo.LoginUser
import com.example.eventcal.ui.theme.EventCalTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {

    private lateinit var apiInterface: APIInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EventCalTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
        apiInterface = APIClient.getClient().create(APIInterface::class.java)
        /*
        var createUser : CreateUser = CreateUser("JaneSchmane@gmail.com", "password")
        createUser(createUser) {
            //do something
            if(it == null) {

            }
            else {
                Toast.makeText(
                    applicationContext,
                    it.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
         */
        var createEvent : Event = Event("OPC Pottery", "OPC", "12:00", "14:00", "10/15/2024", "Union 342", "Come do pottery with the OPC")
        createEvent(createEvent) {
            //do something
            if(it == null) {

            }
            else {
                Toast.makeText(
                    applicationContext,
                    it.message.toString() + " " + it.eventId.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        var loginUser : LoginUser =
            LoginUser(
                "CamdenMcCoy@my.unt.edu",
                "wrongPass"
            )
        /*
        loginUser(loginUser) {
            //Where you can check the response "it"
            if(it == null) {
                print("Hmm")
            }
            else {
                if(it.confirmed == "True") {
                    Toast.makeText(
                        applicationContext,
                        "Login Successful",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else {
                    Toast.makeText(
                        applicationContext,
                        "Login Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }
        */
    }

    //Attempts to create a user with the given userInfo.
    //Kind of returns (does something weird with calling "it" after)
    //the Userinfo result from server, so can compare
    //to make sure the attempt succeeded.
    private fun loginUser(loginUserInfo : LoginUser, onResult : (LoginUser?) -> Unit) {
        var loginUser : LoginUser = loginUserInfo
        val call = apiInterface.login(loginUser)
        call.enqueue(object : Callback<LoginUser> {
            override fun onResponse(call: Call<LoginUser>, response: Response<LoginUser>) {
                val user1 = response.body()
                //Check to see the returned User Data is valid?
                onResult(user1)
            }

            override fun onFailure(call: Call<LoginUser>, t: Throwable) {
                onResult(null)
            }
        })
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

    private fun createEvent(eventInfo : Event, onResult : (Event?) -> Unit) {
        var createEvent : Event = eventInfo
        val call = apiInterface.createEvent(createEvent)
        call.enqueue(object : Callback<Event> {
            override fun onResponse(call: Call<Event>, response: Response<Event>) {
                val event = response.body()
                //Check to see the returned User Data is valid?
                onResult(event)
            }

            override fun onFailure(call: Call<Event>, t: Throwable) {
                onResult(null)
            }
        })
    }

    private fun doGetEventList(date : String, onResult : (EventList?) -> Unit) {
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
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EventCalTheme {
        Greeting("Android")
    }
}
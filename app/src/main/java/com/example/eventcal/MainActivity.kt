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
import com.example.eventcal.pojo.User
import com.example.eventcal.pojo.UserList
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
        var user : User = User("CamdenMcCoy@my.unt.edu", "testPass")
        createUser(user) {
            //Where you can check the response "it"
        }
    }

    private fun doGetUserList() {
        val call = apiInterface.doGetUserList()
        call.enqueue(object : Callback<UserList> {
            override fun onResponse(call: Call<UserList>, response: Response<UserList>) {
                if (response.isSuccessful && response.body() != null) {
                    // TODO: Process data
                    // Maybe get the user data and print it?
                }
            }

            override fun onFailure(call: Call<UserList>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    //Attempts to create a user with the given userInfo.
    //Kind of returns (does something weird with calling "it" after)
    //the Userinfo result from server, so can compare
    //to make sure the attempt succeeded.
    private fun createUser(userInfo : User, onResult : (User?) -> Unit) {
        var user : User = userInfo
        val call = apiInterface.createUser(user)
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                val user1 = response.body()
                //Check to see the returned User Data is valid?
                onResult(user1)
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
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
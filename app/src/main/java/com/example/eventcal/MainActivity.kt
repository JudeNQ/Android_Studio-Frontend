package com.example.eventcal

import android.os.Bundle
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
import com.example.eventcal.ui.theme.EventCalTheme

import com.example.eventcal.pojo.CreateUserResponse
import com.example.eventcal.pojo.MultipleResource
import com.example.eventcal.pojo.User
import com.example.eventcal.pojo.UserList

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
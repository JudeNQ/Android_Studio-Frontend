package com.example.eventcal.pages.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.eventcal.APIClient
import com.example.eventcal.APIInterface
import com.example.eventcal.MainActivity
import com.example.eventcal.R
import com.example.eventcal.dataCoordinator.DataCoordinator
import com.example.eventcal.databinding.LoginPageBinding
import com.example.eventcal.pojo.LoginUser
import com.example.eventcal.userStorage.UserData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


lateinit var apiInterface : APIInterface

class LoginPage : AppCompatActivity() {
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    lateinit var binding: LoginPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiInterface = APIClient.getClient().create(APIInterface::class.java)
        val button = findViewById<Button>(R.id.login_button)

        //Set up the userData
        setUpUserData()

        button.setOnClickListener {
            //Get the text from the text fields
            val email = findViewById<EditText>(R.id.email_text).text.toString()
            val password = findViewById<EditText>(R.id.password_text).text.toString()

            val login : LoginUser = LoginUser(email, password)
            loginUser(login) {
                if (it != null) {
                    //The attempt worked
                    if(it.confirmed == "True") {
                        validLogin(it)
                    }
                    else {
                        //Display toast message saying incorrect information
                        Toast.makeText(
                            applicationContext,
                            "Incorrect email or password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                else {
                    //Display a toast message saying error connecting to server
                    Toast.makeText(
                        applicationContext,
                        "Couldn't connect to the server. Try again later",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }


    }

    //Attempts to create a user with the given userInfo.
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

    fun validLogin(user : LoginUser) {
        //Store the user data
        UserData.shared.userId = user.id

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun setUpUserData() {
        UserData.shared.initialize(
            baseContext,
        )
    }
}
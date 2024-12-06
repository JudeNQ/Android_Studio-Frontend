package com.example.eventcal.pages.signup


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eventcal.APIClient
import com.example.eventcal.APIInterface
import com.example.eventcal.MainActivity
import com.example.eventcal.R
import com.example.eventcal.databinding.SignupPageBinding
import com.example.eventcal.pages.login.LoginPage
import com.example.eventcal.pojo.CreateUser
import com.example.eventcal.pojo.LoginUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupPage : AppCompatActivity() {

    lateinit var binding: SignupPageBinding
    lateinit var apiInterface : APIInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SignupPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiInterface = APIClient.getClient().create(APIInterface::class.java)

        val button = findViewById<Button>(R.id.signup_button)
        button.setOnClickListener {
            //Get the text from the text fields
            val name = findViewById<EditText>(R.id.name_text).text.toString()
            val email = findViewById<EditText>(R.id.email_text).text.toString()
            val password = findViewById<EditText>(R.id.password_text).text.toString()

            if(name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Please fill out all fields",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val signup : CreateUser = CreateUser(name, email, password)
            createUser(signup) {
                if (it != null) {
                    //The attempt worked
                    if(it.message == "") {
                        //Make the text fields empty
                        findViewById<EditText>(R.id.name_text).text.clear()
                        findViewById<EditText>(R.id.email_text).text.clear()
                        findViewById<EditText>(R.id.password_text).text.clear()

                        //Move back to the login page
                        validSignup()
                    }
                    else {
                        //Display toast message saying incorrect information
                        Toast.makeText(
                            applicationContext,
                            "Error: " + it.message.toString(),
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

    fun validSignup() {
        val intent = Intent(this, LoginPage::class.java)
        startActivity(intent)
    }
}

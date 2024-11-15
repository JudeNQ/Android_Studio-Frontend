package com.example.eventcal.pages.profile


import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.eventcal.R

class ProfileActivity : AppCompatActivity() {

    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val usernameTextView: TextView = findViewById(R.id.username_text)
        val biographyTextView: TextView = findViewById(R.id.biography_text)
        val eventsCountTextView: TextView = findViewById(R.id.events_count)
        val organizationsCountTextView: TextView = findViewById(R.id.organizations_count)

        profileViewModel.username.observe(this) { username ->
            usernameTextView.text = username
        }

        profileViewModel.biography.observe(this) { bio ->
            biographyTextView.text = bio
        }

        profileViewModel.eventsCount.observe(this) { count ->
            eventsCountTextView.text = count.toString()
        }

        profileViewModel.organizationsCount.observe(this) { count ->
            organizationsCountTextView.text = count.toString()
        }
    }
}

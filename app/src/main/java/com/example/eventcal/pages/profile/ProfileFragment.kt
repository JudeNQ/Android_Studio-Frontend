package com.example.eventcal.pages.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.eventcal.MainActivity
import com.example.eventcal.R
import com.example.eventcal.pages.login.LoginPage
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import com.example.eventcal.APIClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.eventcal.APIInterface
import com.example.eventcal.pages.profile.EditProfileActivity
import com.example.eventcal.pages.profile.SettingsActivity
import com.example.eventcal.databinding.FragmentProfileBinding
import com.example.eventcal.pojo.ProfileResponse





class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // Initialize the ProfileViewModel
    private val profileViewModel: ProfileViewModel by lazy {
        ViewModelProvider(this).get(ProfileViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val mainActivity: MainActivity = activity as MainActivity

        // Initialize Views
        val usernameTextView: TextView = root.findViewById(R.id.username_text)
        val biographyTextView: TextView = root.findViewById(R.id.biography_text)
        val eventsCountTextView: TextView = root.findViewById(R.id.events_count)
        val organizationsCountTextView: TextView = root.findViewById(R.id.organizations_count)

        val editProfileButton: Button = root.findViewById(R.id.btn_edit_profile)
        val shareProfileButton: Button = root.findViewById(R.id.btn_share_profile)
        val settingsButton: Button = root.findViewById(R.id.btn_settings)
        val logoutButton: Button = root.findViewById(R.id.btn_logout)

        // Observe data from ViewModel
        profileViewModel.username.observe(viewLifecycleOwner) { username ->
            usernameTextView.text = username
        }

        profileViewModel.biography.observe(viewLifecycleOwner) { bio ->
            biographyTextView.text = bio
        }

        profileViewModel.eventsCount.observe(viewLifecycleOwner) { count ->
            eventsCountTextView.text = count.toString()
        }

        profileViewModel.organizationsCount.observe(viewLifecycleOwner) { count ->
            organizationsCountTextView.text = count.toString()
        }

        // Handle Edit Profile button click
        editProfileButton.setOnClickListener {
            val intent = Intent(mainActivity, EditProfileActivity::class.java)
            startActivity(intent)
        }

        // Handle Share Profile button click
        shareProfileButton.setOnClickListener {
            Toast.makeText(mainActivity, "Share Profile clicked", Toast.LENGTH_SHORT).show()
        }

        // Handle Settings button click
        settingsButton.setOnClickListener {
            val intent = Intent(mainActivity, SettingsActivity::class.java)
            startActivity(intent)
        }

        // Handle logout button click
        logoutButton.setOnClickListener {
            val sharedPreferences = mainActivity.getSharedPreferences("PlanItPrefs", AppCompatActivity.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            val intent = Intent(mainActivity, LoginPage::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

            Toast.makeText(mainActivity, "Logged out successfully", Toast.LENGTH_SHORT).show()
        }

        // Fetch the user's profile data from the backend
        fetchProfileData("user_id_here")

        return root
    }

    // Function to fetch profile data
    private fun fetchProfileData(userId: String) {
        val apiInterface = APIClient.getClient().create(APIInterface::class.java)
        val call = apiInterface.getProfile(userId)
        call.enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful) {
                    val profile = response.body()
                    if (profile != null) {
                        // Update ViewModel with profile data
                        profileViewModel.updateUsername(profile.username)
                        profileViewModel.updateBiography(profile.biography)
                        profileViewModel.updateEventCount(profile.eventsCount)
                        profileViewModel.updateOrganizationCount(profile.organizationsCount)
                    }
                } else {
                    Toast.makeText(activity, "Failed to fetch profile", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Toast.makeText(activity, "Error fetching profile data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.example.eventcal.pages.profile


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.eventcal.MainActivity
import com.example.eventcal.R
import com.example.eventcal.adapters.EventAdapter
import com.example.eventcal.databinding.FragmentHomeBinding
import com.example.eventcal.databinding.FragmentProfileBinding
import com.example.eventcal.pages.home.HomeViewModel

class ProfileFragment : Fragment() {

    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val mainActivity : MainActivity = activity as MainActivity

        val usernameTextView: TextView = root.findViewById(R.id.username_text)
        val biographyTextView: TextView = root.findViewById(R.id.biography_text)
        val eventsCountTextView: TextView = root.findViewById(R.id.events_count)
        val organizationsCountTextView: TextView = root.findViewById(R.id.organizations_count)

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

        return root;
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}

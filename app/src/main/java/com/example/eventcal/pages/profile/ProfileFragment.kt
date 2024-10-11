package com.example.eventcal.pages.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.eventcal.databinding.FragmentHomeBinding
import com.example.eventcal.pages.home.HomeViewModel

class ProfileFragment : Fragment() {
    private var _binding : FragmentHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //Add code for displaying profile or whatever

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
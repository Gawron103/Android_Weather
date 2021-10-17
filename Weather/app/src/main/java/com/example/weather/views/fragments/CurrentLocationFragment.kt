package com.example.weather.views.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weather.R
import com.example.weather.databinding.FragmentCurrentLocationBinding

class CurrentLocationFragment : Fragment() {

    private val TAG = "CurrentLocationFragment"

    private var _binding: FragmentCurrentLocationBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCurrentLocationBinding.inflate(inflater, container, false)

        binding.slCurrentLocation.setOnRefreshListener {
            Log.d(TAG, "Refreshing current weather")
            binding.slCurrentLocation.isRefreshing = false
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
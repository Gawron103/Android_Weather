package com.example.weather.views.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.weather.databinding.FragmentCitiesPageBinding

class CitiesPageFragment : Fragment() {

    private var _binding: FragmentCitiesPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCitiesPageBinding.inflate(inflater, container, false)
        return binding.root
    }

}
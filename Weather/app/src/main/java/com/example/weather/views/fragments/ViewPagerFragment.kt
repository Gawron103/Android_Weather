package com.example.weather.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weather.R
import com.example.weather.databinding.FragmentDetailedWeatherForCityBinding
import com.example.weather.databinding.FragmentViewPagerBinding
import com.example.weather.views.adapters.ViewPagerAdapter

class ViewPagerFragment : Fragment() {


    private var _binding: FragmentViewPagerBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentViewPagerBinding.inflate(inflater, container, false)

        val fragments = arrayListOf<Fragment>(
            CurrentLocationFragment(),
            CitiesPageFragment()
        )

        val adapter = ViewPagerAdapter(
            fragments,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        binding.viewPager.adapter = adapter

        return binding.root
    }

}
package com.example.weather.views.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.R
import com.example.weather.databinding.FragmentCitiesListBinding
import com.example.weather.models.CityModel
import com.example.weather.networking.PlacesApi
import com.example.weather.networking.WeatherApi
import com.example.weather.db.DbConfig
import com.example.weather.repositories.WeatherRepository
import com.example.weather.viewmodels.CitiesDataViewModel
import com.example.weather.viewmodels.CitiesDataViewModelFactory
import com.example.weather.views.adapters.CitiesListAdapter

class CitiesListFragment : Fragment() {

    private val TAG = "CitiesListFragment"

    val args: CitiesListFragmentArgs by navArgs()

    private var _binding: FragmentCitiesListBinding? = null
    private val binding get() = _binding!!

    private lateinit var _viewModel: CitiesDataViewModel
    private lateinit var citiesWeatherListAdapter: CitiesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate triggered")
        super.onCreate(savedInstanceState)

        val placesService = PlacesApi.getInstance()
        val weatherService = WeatherApi.getInstance()
        val weatherRepository = WeatherRepository(weatherService, placesService, DbConfig.provideRealmConfig())

        citiesWeatherListAdapter = CitiesListAdapter(arrayListOf(), ::deleteCity, requireContext())

        _viewModel = ViewModelProvider(
            this,
            CitiesDataViewModelFactory(weatherRepository)
        ).get(CitiesDataViewModel::class.java)

        _viewModel.refresh()
        
        args.newCity?.let { name ->
            Log.d(TAG, "Should add city: $name")
            addCity(name)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView triggered")
        _binding = FragmentCitiesListBinding.inflate(inflater, container, false)

        binding.rvCitiesList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = citiesWeatherListAdapter
        }

        binding.srCitiesList.setOnRefreshListener {
            _viewModel.refresh()
            binding.srCitiesList.isRefreshing = false
        }

        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_citiesListFragment_to_addCityFragment)
        }

        observeWeatherViewModel()

        Log.d(TAG, "NavStack: ${findNavController().backStack}")

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView triggered")
        _binding = null
    }

    private fun observeWeatherViewModel() {
        _viewModel.citiesLiveData.observe(requireActivity(), Observer { cities ->
            binding.tvNoCities.visibility = if (cities.isEmpty()) View.VISIBLE else View.GONE
            citiesWeatherListAdapter.updateCities(cities)
        })

        _viewModel.weatherLoading.observe(requireActivity(), Observer { isLoading ->
            binding.pbLoading.visibility = if(isLoading) View.VISIBLE else View.GONE
            binding.tvNoCities.visibility = View.GONE
        })

        _viewModel.cityAdded.observe(requireActivity(), Observer { cityAdded ->
            val message = if (cityAdded) "New city added" else "City already exists"
            Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show()
        })

        _viewModel.isRequestCorrect.observe(requireActivity(), Observer { isRequestCorrect ->
            if (!isRequestCorrect) {
                Toast.makeText(requireActivity(), "Wrong input", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun addCity(name: String) {
        _viewModel.addCity(name)
    }

    private fun deleteCity(cityModel: CityModel) {
        _viewModel.removeCity(cityModel)
    }

}
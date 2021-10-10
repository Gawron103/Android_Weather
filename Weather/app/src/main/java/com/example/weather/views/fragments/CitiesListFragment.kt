package com.example.weather.views.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.R
import com.example.weather.databinding.FragmentCitiesListBinding
import com.example.weather.db.CityDatabase
import com.example.weather.models.CityModel
import com.example.weather.networking.PlacesApi
import com.example.weather.networking.WeatherApi
import com.example.weather.repositories.WeatherRepository
import com.example.weather.viewmodels.CitiesDataViewModel
import com.example.weather.viewmodels.CitiesDataViewModelFactory
import com.example.weather.views.adapters.CitiesListAdapter

class CitiesListFragment : Fragment() {

    private val TAG = "CitiesListFragment"

    private var _binding: FragmentCitiesListBinding? = null
    private val binding get() = _binding!!

    private val args: CitiesListFragmentArgs by navArgs()

    private lateinit var _viewModel: CitiesDataViewModel
    private lateinit var citiesWeatherListAdapter: CitiesListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCitiesListBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val cityDao = CityDatabase.getInstance(requireActivity()).cityDAO
        val placesService = PlacesApi.getInstance()
        val weatherService = WeatherApi.getInstance()
        val weatherRepository = WeatherRepository(weatherService, placesService, cityDao)

        citiesWeatherListAdapter = CitiesListAdapter(arrayListOf(), ::deleteCity, requireContext())

        _viewModel = ViewModelProvider(
            this,
            CitiesDataViewModelFactory(weatherRepository)
        ).get(CitiesDataViewModel::class.java)

        binding.rvCitiesList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = citiesWeatherListAdapter
        }

        binding.srCitiesList.setOnRefreshListener {
            _viewModel.refresh()
            binding.srCitiesList.isRefreshing = false
        }

        binding.btnRetry.setOnClickListener {
//            _viewModel.refresh()
        }

        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_AddCity, null)
        }

        _viewModel.refresh()

        args.newCityNameArg?.let { name ->
            addCity(name)
        }

        observeWeatherViewModel()
    }

    private fun observeWeatherViewModel() {
        _viewModel.citiesLiveData.observe(requireActivity(), Observer {
            if (true == it?.isEmpty()) {
                citiesWeatherListAdapter.updateCities(listOf())
                binding.tvNoCities.visibility = View.VISIBLE
            }
            else {
                citiesWeatherListAdapter.updateCities(it)
                binding.tvNoCities.visibility = View.GONE
            }

            binding.btnRetry.visibility = View.GONE
        })

        _viewModel.weatherLoading.observe(requireActivity(), Observer { isLoading ->
            isLoading?.let {
                binding.pbLoading.visibility = if(isLoading) View.VISIBLE else View.GONE
                binding.btnAdd.visibility = if (isLoading) View.GONE else View.VISIBLE

                if(isLoading) {
                    binding.btnRetry.visibility = View.GONE
                    binding.tvNoCities.visibility = View.GONE
                }
                else {
                    context?.setTheme(R.style.Theme_AppCompat_Light_NoActionBar)
                }
            }
        })

        _viewModel.cityExists.observe(requireActivity(), Observer { cityExists ->
            if (cityExists) {
                Toast.makeText(requireActivity(), "City already exists", Toast.LENGTH_LONG).show()
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
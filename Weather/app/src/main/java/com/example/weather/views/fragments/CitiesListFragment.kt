package com.example.weather.views.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.databinding.FragmentCitiesListBinding
import com.example.weather.networking.PlacesApi
import com.example.weather.networking.WeatherApi
import com.example.weather.repositories.WeatherRepository
import com.example.weather.viewmodels.CitiesDataViewModel
import com.example.weather.viewmodels.CitiesDataViewModelFactory
import com.example.weather.views.adapters.CitiesListAdapter

class CitiesListFragment : Fragment() {

    private val TAG = "CitiesListFragment"

    private val args: CitiesListFragmentArgs by navArgs()

    private var _binding: FragmentCitiesListBinding? = null
    private val binding get() = _binding!!

    private lateinit var _viewModel: CitiesDataViewModel
    private lateinit var _citiesWeatherListAdapter: CitiesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val placesService = PlacesApi.getInstance()
        val weatherService = WeatherApi.getInstance()
        val weatherRepository = WeatherRepository(weatherService, placesService)

        _citiesWeatherListAdapter = CitiesListAdapter(arrayListOf(),
            requireContext(),
            ::deleteBtnVisibilityCallback)

        _viewModel = ViewModelProvider(
            this,
            CitiesDataViewModelFactory(weatherRepository)
        ).get(CitiesDataViewModel::class.java)

        args.newCity?.let { name ->
            addCity(name)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCitiesListBinding.inflate(inflater, container, false)

        binding.rvCitiesList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = _citiesWeatherListAdapter
        }

        getItemTouchHelper().also { helper ->
            helper.attachToRecyclerView(binding.rvCitiesList)
            Log.d(TAG, "helper attached to the recycler view")
        }

        binding.btnRemoveSelected.setOnClickListener {
            val cities = _citiesWeatherListAdapter.removeSelectedItems()
            _citiesWeatherListAdapter.resetSelectedItems()
            _viewModel.removeSelectedCities(cities)
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
        _binding = null
    }

    private fun getItemTouchHelper(): ItemTouchHelper {
        return ItemTouchHelper(
            object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val item = _citiesWeatherListAdapter.getItemAt(position)

                    deleteCity(item.locationModel?.get(0)?.cityName!!)
                    _citiesWeatherListAdapter.removeItemAt(position)
                    _citiesWeatherListAdapter.notifyItemRemoved(position)
                }
            }
        )
    }

    private fun observeWeatherViewModel() {
        _viewModel.citiesLiveData.observe(viewLifecycleOwner, { cities ->
            _citiesWeatherListAdapter.updateCities(cities)
        })

        _viewModel.weatherLoading.observe(viewLifecycleOwner, { isLoading ->
            binding.pbLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.rvCitiesList.visibility = if (isLoading) View.GONE else View.VISIBLE
                binding.btnAdd.visibility = if (isLoading) View.GONE else View.VISIBLE
        })

        _viewModel.cityAdded.observe(viewLifecycleOwner, { cityAdded ->
            val message = if (cityAdded) "New city added" else "City already exists"
            Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show()
        })

        _viewModel.cityDeleted.observe(viewLifecycleOwner, { cityDeleted ->
            val message = if (cityDeleted) "City deleted" else "Cannot delete the city"
            Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show()
        })
    }

    private fun addCity(name: String) {
        _viewModel.addCity(name)
    }

    private fun deleteCity(cityName: String) {
        _viewModel.removeCity(cityName)
    }

    private fun deleteBtnVisibilityCallback(isVisible: Boolean) {
        binding.btnRemoveSelected.visibility = when (isVisible) {
            true -> { View.VISIBLE }
            else -> { View.GONE }
        }
    }

}
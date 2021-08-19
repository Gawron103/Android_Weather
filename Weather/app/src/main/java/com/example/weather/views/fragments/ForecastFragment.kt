package com.example.weather.views.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.networking.WeatherApi
import com.example.weather.networking.WeatherRepository
import com.example.weather.viewmodels.ViewModelFactory
import com.example.weather.viewmodels.WeatherSharedViewModel
import com.example.weather.views.adapters.ForecastListAdapter

class ForecastFragment : Fragment() {

    companion object {
        fun newInstance() = ForecastFragment()
    }

    private lateinit var viewModel: WeatherSharedViewModel
    private val forecastListAdapter = ForecastListAdapter(arrayListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.forecast_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val weatherService = WeatherApi.getInstance()
        val repository = WeatherRepository(weatherService)
        viewModel = ViewModelProvider(requireActivity(), ViewModelFactory(repository)).get(WeatherSharedViewModel::class.java)

        val forecastList = view?.findViewById<RecyclerView>(R.id.forecastList)
        forecastList?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = forecastListAdapter
        }

        observeModel()
    }

    private fun observeModel() {
        viewModel.weather.observe(viewLifecycleOwner, Observer { weather ->
            weather?.let { forecastListAdapter.updateWeather(it.dailyConditions!!) }
        })

        viewModel.weatherLoadError.observe(viewLifecycleOwner, Observer { isError ->
            isError?.let {
                view?.findViewById<TextView>(R.id.fetch_error)?.visibility = if(it) View.VISIBLE else View.GONE
            }
        })

        viewModel.weatherLoadError.observe(viewLifecycleOwner, Observer { isLoading ->
            isLoading?.let {
                view?.findViewById<ProgressBar>(R.id.view_load)?.visibility = if(it) View.VISIBLE else View.GONE

                if(it) {
                    view?.findViewById<TextView>(R.id.fetch_error)?.visibility = View.GONE
                    view?.findViewById<RecyclerView>(R.id.forecastList)?.visibility = View.GONE
                }
            }
        })
    }

}
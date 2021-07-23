package com.example.weather.views

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
import com.example.weather.models.CurrentWeatherModel
import com.example.weather.viewmodels.CurrentWeatherViewModel
import com.example.weather.views.adapters.CurrentWeatherAdapter

class CurrentWeatherFragment : Fragment() {

    companion object {
        fun newInstance() = CurrentWeatherFragment()
    }

    private lateinit var viewModel: CurrentWeatherViewModel
    private val currentWeatherAdapter = CurrentWeatherAdapter(arrayListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.current_weather_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CurrentWeatherViewModel::class.java)
        viewModel.refresh()

        val countriesList = view?.findViewById<RecyclerView>(R.id.currentWeatherList)
        countriesList?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = currentWeatherAdapter
        }

        observeViewModel()
    }

    fun observeViewModel() {
        viewModel.currentWeather.observe(viewLifecycleOwner, Observer { weather ->
            weather?.let { currentWeatherAdapter.updateWeather(it) }

        })

        viewModel.currentWeatherLoadError.observe(viewLifecycleOwner, Observer { isError ->
            isError?.let {
                view?.findViewById<TextView>(R.id.fetch_error)?.visibility = if(it) View.VISIBLE else View.GONE
            }
        })

        viewModel.loading.observe(viewLifecycleOwner, Observer { isLoading ->
            isLoading?.let {
                view?.findViewById<ProgressBar>(R.id.view_load)?.visibility = if(it) View.VISIBLE else View.GONE

                if(it) {
                    view?.findViewById<TextView>(R.id.fetch_error)?.visibility = View.GONE
                    view?.findViewById<RecyclerView>(R.id.currentWeatherList)?.visibility = View.GONE
                }
            }
        })
    }
}
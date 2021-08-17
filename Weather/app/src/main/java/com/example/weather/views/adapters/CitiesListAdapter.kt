package com.example.weather.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.models.TestModel
import com.example.weather.models.current_weather_model.Current
import com.example.weather.models.current_weather_model.Daily

class CitiesListAdapter(private var citiesList: ArrayList<TestModel>): RecyclerView.Adapter<CitiesListAdapter.CitiesListViewHolder>() {

    fun updateCities(newCities: List<TestModel>) {
        citiesList.clear()
        citiesList.addAll(newCities)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CitiesListViewHolder (
        LayoutInflater.from(parent.context).inflate(R.layout.item_city_short_weather, parent, false)
    )

    override fun onBindViewHolder(holder: CitiesListViewHolder, position: Int) {
        holder.bind(citiesList[position])
    }

    override fun getItemCount(): Int = citiesList.size

    class CitiesListViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val cityName = view.findViewById<TextView>(R.id.tv_locationName)
        private val countryCode = view.findViewById<TextView>(R.id.tv_countryCode)
        private val tempVal = view.findViewById<TextView>(R.id.tv_tempVal)
        private val humidityVal = view.findViewById<TextView>(R.id.tv_humidityVal)
        private val minTempVal = view.findViewById<TextView>(R.id.tv_minTemp)
        private val maxTempVal = view.findViewById<TextView>(R.id.tv_maxTemp)

        fun bind(modelForLocation: TestModel) {
            cityName.text = modelForLocation.locationModel?.get(0)?.cityName
            countryCode.text = modelForLocation.locationModel?.get(0)?.countryCode
            tempVal.text = modelForLocation.weatherModel?.dailyConditions?.get(0)?.tempInDay?.day.toString()
            humidityVal.text = modelForLocation.weatherModel?.currentConditions?.humidity.toString()
            minTempVal.text = modelForLocation.weatherModel?.dailyConditions?.get(0)?.tempInDay?.min.toString()
            maxTempVal.text = modelForLocation.weatherModel?.dailyConditions?.get(0)?.tempInDay?.max.toString()
        }

    }

}
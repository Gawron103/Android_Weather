package com.example.weather.views.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.BuildConfig
import com.example.weather.R
import com.example.weather.db.City
import com.example.weather.models.TestModel
import com.example.weather.repositories.CityRepository
import com.example.weather.views.fragments.DetailedWeatherFragment
import com.example.weather.views.interfaces.Communicator
import com.example.weather.views.interfaces.DatabaseCommunicator
import kotlin.math.round

class CitiesListAdapter(
    private var citiesList: ArrayList<TestModel>,
    private val communicator: Communicator,
    private val databaseCommunicator: DatabaseCommunicator
):
    RecyclerView.Adapter<CitiesListAdapter.CitiesListViewHolder>() {

    fun updateCities(newCities: List<TestModel>) {
        citiesList.clear()
        citiesList.addAll(newCities)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CitiesListViewHolder (
        LayoutInflater.from(parent.context).inflate(R.layout.item_city_short_weather, parent, false),
        parent.context
    )

    override fun onBindViewHolder(holder: CitiesListViewHolder, position: Int) {
        holder.bind(citiesList[position])
    }

    override fun getItemCount(): Int = citiesList.size

    inner class CitiesListViewHolder(view: View, private val context: Context): RecyclerView.ViewHolder(view) {

        private val TAG = "CitiesListViewHolder"

        private val cityImg = view.findViewById<ImageView>(R.id.iv_city)
        private val cityName = view.findViewById<TextView>(R.id.tv_locationName)
        private val countryCode = view.findViewById<TextView>(R.id.tv_countryCode)
        private val tempVal = view.findViewById<TextView>(R.id.tv_tempVal)
        private val humidityVal = view.findViewById<TextView>(R.id.tv_humidityVal)
        private val minTempVal = view.findViewById<TextView>(R.id.tv_minTemp)
        private val maxTempVal = view.findViewById<TextView>(R.id.tv_maxTemp)
        private val deleteBtn = view.findViewById<Button>(R.id.btn_deleteCity)

        fun bind(modelForLocation: TestModel) {
            Glide.with(context).load(
                "https://maps.googleapis.com/maps/api/place/photo?photoreference=${modelForLocation.placesModel?.candidates?.get(0)?.photos?.get(0)?.photo_reference}&key=${BuildConfig.PLACES_API_KEY}&maxwidth=1980&maxheight=1200"
            ).error(R.drawable.error_icon).into(cityImg)

            cityName.text = modelForLocation.locationModel?.get(0)?.cityName
            countryCode.text = modelForLocation.locationModel?.get(0)?.countryCode
            tempVal.text = round(modelForLocation.weatherModel?.dailyConditions?.get(0)?.tempInDay?.day!!).toString()
            humidityVal.text = modelForLocation.weatherModel?.currentConditions?.humidity.toString()
            minTempVal.text = round(modelForLocation.weatherModel?.dailyConditions?.get(0)?.tempInDay?.min!!).toString()
            maxTempVal.text = round(modelForLocation.weatherModel?.dailyConditions?.get(0)?.tempInDay?.max!!).toString()

            itemView.setOnClickListener {
                communicator.pushFragment(DetailedWeatherFragment(modelForLocation), DetailedWeatherFragment.TAG)
                Log.d(TAG, "${cityName.text} clicked")
            }

            deleteBtn.setOnClickListener {
                databaseCommunicator.deleteCity(
                    City(modelForLocation.idInDb!!, modelForLocation.locationModel?.get(0)?.cityName!!)
                )
                Log.d(TAG, "Delete icon clicked")
                Log.d(TAG, "Deteled ${modelForLocation.locationModel?.get(0)?.cityName!!} with id ${modelForLocation.idInDb!!}")
            }
        }
    }

}
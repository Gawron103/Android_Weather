package com.example.weather.views.adapters

import android.content.Context
import android.content.Intent
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
import com.example.weather.models.CityModel
import com.example.weather.utils.Const
import com.example.weather.views.activities.DetailedWeatherActivity
import kotlin.math.round

class CitiesListAdapter(
    private var citiesList: ArrayList<CityModel>,
    private val onDeleteCallback: (CityModel) -> Unit
):
    RecyclerView.Adapter<CitiesListAdapter.CitiesListViewHolder>()
{

    fun updateCities(newCities: List<CityModel>) {
        citiesList.clear()
        citiesList.addAll(newCities)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CitiesListViewHolder (
        LayoutInflater.from(parent.context).inflate(R.layout.item_city_short_weather, parent, false),
        parent.context
    )

    override fun onBindViewHolder(holder: CitiesListViewHolder, position: Int) {
        holder.bind(citiesList[position], position)
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
        private val localizationIcon = view.findViewById<ImageView>(R.id.iv_localizationIcon)

        fun bind(modelForLocation: CityModel, position: Int) {
            Glide.with(context).load(
                "https://maps.googleapis.com/maps/api/place/photo?photoreference=${modelForLocation.placesModel?.candidates?.get(0)?.photos?.get(0)?.photo_reference}&key=${BuildConfig.PLACES_API_KEY}&maxwidth=1980&maxheight=1200"
            ).error(R.drawable.error_icon).into(cityImg)

            if (Const.CURRENT_LOCATION_POSITION == position) deleteBtn.visibility = View.GONE else localizationIcon.visibility = View.GONE

            cityName.text = modelForLocation.locationModel?.get(0)?.cityName
            countryCode.text = modelForLocation.locationModel?.get(0)?.countryCode
            tempVal.text = round(modelForLocation.weatherModel?.dailyConditions?.get(0)?.tempInDay?.day!!).toString()
            humidityVal.text = modelForLocation.weatherModel?.currentConditions?.humidity.toString()
            minTempVal.text = round(modelForLocation.weatherModel?.dailyConditions?.get(0)?.tempInDay?.min!!).toString()
            maxTempVal.text = round(modelForLocation.weatherModel?.dailyConditions?.get(0)?.tempInDay?.max!!).toString()

            itemView.setOnClickListener {
                val intent = Intent(context, DetailedWeatherActivity::class.java)
                intent.putExtra("weather_model", modelForLocation.weatherModel)
                intent.putExtra("city_name", modelForLocation.locationModel?.get(0)?.cityName)
                intent.putExtra("country_code", modelForLocation.locationModel?.get(0)?.countryCode)
                Log.d(TAG, "${cityName.text} clicked")
                context.startActivity(intent)
            }

            deleteBtn.setOnClickListener {
                onDeleteCallback(modelForLocation)
                Log.d(TAG, "Delete icon clicked")
            }
        }

    }

}
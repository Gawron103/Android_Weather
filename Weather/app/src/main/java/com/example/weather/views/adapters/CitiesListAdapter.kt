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
import com.example.weather.databinding.ItemCityShortWeatherBinding
import com.example.weather.models.CityModel
import com.example.weather.utils.Const
import com.example.weather.views.activities.DetailedWeatherActivity
import kotlin.math.round

class CitiesListAdapter(
    private var citiesList: ArrayList<CityModel>,
    private val onDeleteCallback: (CityModel) -> Unit,
    private val context: Context
):
    RecyclerView.Adapter<CitiesListAdapter.CitiesListViewHolder>()
{

    private val TAG = "CitiesListAdapter"

    fun updateCities(newCities: List<CityModel>) {
        citiesList.clear()
        citiesList.addAll(newCities)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CitiesListViewHolder(
        ItemCityShortWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: CitiesListViewHolder, position: Int) {
        holder.binding.tvLocationName.text = citiesList[position].locationModel?.get(0)?.cityName
        holder.binding.tvCountryCode.text =  citiesList[position].locationModel?.get(0)?.countryCode
        holder.binding.tvTempVal.text = round(citiesList[position].weatherModel?.currentConditions?.temp!!).toString()
        holder.binding.tvHumidityVal.text = citiesList[position].weatherModel?.currentConditions?.humidity.toString()
        holder.binding.tvMinTemp.text = round(citiesList[position].weatherModel?.dailyConditions?.get(0)?.tempInDay?.min!!).toString()
        holder.binding.tvMaxTemp.text = round(citiesList[position].weatherModel?.dailyConditions?.get(0)?.tempInDay?.max!!).toString()

        Glide.with(context).load(
            "https://maps.googleapis.com/maps/api/place/photo?photoreference=${citiesList[position].placesModel?.candidates?.get(0)?.photos?.get(0)?.photo_reference}&key=${BuildConfig.PLACES_API_KEY}&maxwidth=1980&maxheight=1200"
        ).error(R.drawable.error_icon).into(holder.binding.ivCity)

        holder.binding.btnDeleteCity.setOnClickListener {
            onDeleteCallback(citiesList[position])
            Log.d(TAG, "Delete icon clicked")
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailedWeatherActivity::class.java)
            intent.putExtra("weather_model", citiesList[position].weatherModel)
            intent.putExtra("city_name", citiesList[position].locationModel?.get(0)?.cityName)
            intent.putExtra("country_code", citiesList[position].locationModel?.get(0)?.countryCode)
            Log.d(TAG, "${citiesList[position].locationModel?.get(0)?.cityName} clicked")
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = citiesList.size

    inner class CitiesListViewHolder(val binding: ItemCityShortWeatherBinding): RecyclerView.ViewHolder(binding.root)

}
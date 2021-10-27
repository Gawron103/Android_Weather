package com.example.weather.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import com.bumptech.glide.Glide
import com.example.weather.R
import com.example.weather.databinding.ItemForecastCurrentLocationBinding
import com.example.weather.models.current_weather_model.Daily
import com.example.weather.utils.DateProvider
import kotlin.math.round

class CurrentLocationForecastAdapter (
    private var _forecastList: ArrayList<Daily>,
    private val _context: Context,
    private val _parent: RecyclerView
):
    RecyclerView.Adapter<CurrentLocationForecastAdapter.ForecastViewHolder>()
{
    fun updateForecast(newForecast: List<Daily>) {
        _forecastList.clear()
        _forecastList.addAll(newForecast)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ForecastViewHolder (
        ItemForecastCurrentLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.bind(_forecastList[position])
    }

    override fun getItemCount(): Int = _forecastList.size

    inner class ForecastViewHolder(private val binding: ItemForecastCurrentLocationBinding): RecyclerView.ViewHolder(binding.root) {

            fun bind(day: Daily) {
                binding.tvDayName.text = DateProvider.getDayName(day.currentTime!!)
                binding.tvTemperature.text = round(day.tempInDay?.day!!).toString()
                binding.tvWindSpeedVal.text = round(day.windSpeed!!).toString()
                binding.tvCloudinessVal.text = day.clouds.toString()
                binding.tvSunriseVal.text = DateProvider.convertTime(day.sunrise!!)
                binding.tvPressureVal.text = day.pressure.toString()
                binding.tvHumidityVal.text = day.humidity.toString()
                binding.tvSunsetVal.text = DateProvider.convertTime(day.sunset!!)

                Glide.with(_context).load(
                    "https://openweathermap.org/img/wn/${day.weather?.get(0)?.icon}@4x.png"
                ).error(R.drawable.error_icon).into(binding.ivWeatherIcon)

                itemView.setOnClickListener {
                    androidx.transition.TransitionManager.beginDelayedTransition(_parent, AutoTransition())
                    binding.clHiddenInfo.visibility = when (binding.clHiddenInfo.visibility) {
                        View.GONE -> {
                            View.VISIBLE
                        }
                        else -> {
                            View.GONE
                        }
                    }
                }
            }

        }

}
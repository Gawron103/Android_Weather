package com.example.weather.views.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.BuildConfig
import com.example.weather.R
import com.example.weather.databinding.ItemCityShortWeatherBinding
import com.example.weather.models.CityModel
import com.example.weather.views.fragments.CitiesListFragmentDirections
import kotlin.math.round
import kotlin.properties.Delegates

class CitiesListAdapter(
    private var citiesList: ArrayList<CityModel>,
    private val context: Context,
    private val deleteBtnVisibilityCallback: (Boolean) -> Unit
):
    RecyclerView.Adapter<CitiesListAdapter.CitiesListViewHolder>()
{

    private val TAG = "CitiesListAdapter"

    private var _selectedItems: Set<String> by Delegates.observable(mutableSetOf()) { _, _, new ->
        val btnShouldBeVisible = new.isNotEmpty()
        deleteBtnVisibilityCallback(btnShouldBeVisible)
    }

    fun updateCities(newCities: List<CityModel>) {
        citiesList.clear()
        citiesList.addAll(newCities)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CitiesListViewHolder(
        ItemCityShortWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: CitiesListViewHolder, position: Int) {
        holder.bind(citiesList[position], context)
    }

    override fun getItemCount(): Int = citiesList.size

    fun getItemAt(position: Int): CityModel = citiesList[position]

    fun removeItemAt(position: Int) {
        citiesList.removeAt(position)
    }

    fun removeSelectedItems(): Set<String> {
        for (city in _selectedItems) {
            citiesList.removeIf { model -> city == model.locationModel?.get(0)?.cityName!! }
        }

        notifyDataSetChanged()

        return _selectedItems
    }

    fun resetSelectedItems() {
        _selectedItems = mutableSetOf()
    }

    inner class CitiesListViewHolder(private val binding: ItemCityShortWeatherBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(cityModel: CityModel, context: Context) {
            binding.tvLocationName.text = cityModel.locationModel?.get(0)?.cityName
            binding.tvCountryCode.text =  cityModel.locationModel?.get(0)?.countryCode
            binding.tvTempVal.text = round(cityModel.weatherModel?.currentConditions?.temp!!).toString()
            binding.tvHumidityVal.text = cityModel.weatherModel.currentConditions.humidity.toString()
            binding.tvMinTemp.text = round(cityModel.weatherModel.dailyConditions?.get(0)?.tempInDay?.min!!).toString()
            binding.tvMaxTemp.text = round(cityModel.weatherModel.dailyConditions[0].tempInDay?.max!!).toString()

            Glide.with(context).load(
                "https://maps.googleapis.com/maps/api/place/photo?photoreference=${cityModel.placesModel?.candidates?.get(0)?.photos?.get(0)?.photo_reference}&key=${BuildConfig.PLACES_API_KEY}&maxwidth=1980&maxheight=1200"
            ).error(R.drawable.error_icon).into(binding.ivCity)

            itemView.setOnClickListener {
                val action = CitiesListFragmentDirections.actionCitiesListFragmentToDetailedWeatherForCityFragment(
                    cityModel.locationModel?.get(0)?.cityName!!,
                    cityModel.locationModel[0].countryCode!!,
                    cityModel.weatherModel
                )

                it.findNavController().navigate(action)
            }

            itemView.setOnLongClickListener {
                binding.ivCheckdIcon.visibility = when (binding.ivCheckdIcon.visibility) {
                    View.GONE -> {
                        _selectedItems += cityModel.locationModel?.get(0)?.cityName!!
                        binding.root.background.setTint(context.getColor(R.color.city_selected))
                        View.VISIBLE
                    }
                    else -> {
                        _selectedItems -= cityModel.locationModel?.get(0)?.cityName!!
                        binding.root.background.setTint(Color.parseColor("#ffffff"))
                        View.GONE
                    }
                }

                Log.d(TAG, "List of selected items: $_selectedItems")

                true
            }
        }

    }

}
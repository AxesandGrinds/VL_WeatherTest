package com.vl.victorlekweuwaweatherapplication.features.weather

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vl.victorlekweuwaweatherapplication.api.GeoCodingApi
import com.vl.victorlekweuwaweatherapplication.api.models.weatherModels.WeatherResponse
import com.vl.victorlekweuwaweatherapplication.data.WeatherRepository
import com.vl.victorlekweuwaweatherapplication.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    companion object {
        const val last_city = "last_city"
    }

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            delay(1000)
            _isLoading.value = false
        }
    }

    var _weatherData = MutableLiveData<WeatherResponse?>()
    var weatherData: LiveData<WeatherResponse?> = _weatherData

    var _state = MutableLiveData<Int>(0)
    var state: LiveData<Int?> = _state

    fun getLastCitySearched(activity: Activity?) : String {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val lastCitySearched = sharedPref?.getString(last_city, "") ?: ""
        return lastCitySearched
    }

    fun putLastCitySearched(activity: Activity?, lastCity: String) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString(last_city, lastCity)
            apply()
        }
    }

    fun clearSharedPreferences(activity: Activity?) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        sharedPref?.edit()?.remove(last_city)?.commit()
    }

    fun getWeather(location: String) = viewModelScope.launch {

        println("ATTENTION ATTENTION: From getWeather(), Determined City: ${location}")

        repository.getWeather(location).collectLatest {

            when (it) {
                is Resource.Loading -> {
                    _state.value = 1
                }
                is Resource.Error -> {
                    println("ATTENTION ATTENTION: Get Error Weather Response: ${it.error?.message}")
                    _state.value = 2
                }
                is Resource.Success -> {
                    println("ATTENTION ATTENTION: Get Successful Weather Response: ${it.data}")
                    _state.value = 3
                    _weatherData.value = it.data
                }
            }

        }

    }

    fun resetState() {
        _state.value = 0
    }

    fun getCityFromLatLon(latitude: Double, longitude: Double, geoCodingApi: GeoCodingApi) {

        println("ATTENTION ATTENTION: From getCityFromLatLon(), Determined Latitude: ${latitude}")
        println("ATTENTION ATTENTION: From getCityFromLatLon(), Determined Longitude: ${longitude}")

        viewModelScope.launch(Dispatchers.IO) {

            val result = geoCodingApi.getCityByLocation(latitude, longitude)

            if (result != null) {
                val cityName = result[0].localNames?.en ?: "Unknown City"
                println("ATTENTION ATTENTION: From getCityFromLatLon(), Determined City: ${cityName}")
                getWeather(cityName)
            }

        }

    }

}
package com.vl.victorlekweuwaweatherapplication.features.weather

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vl.victorlekweuwaweatherapplication.api.GeoCodingApi
import com.vl.victorlekweuwaweatherapplication.api.models.weatherModels.WeatherResponse
import com.vl.victorlekweuwaweatherapplication.data.WeatherRepository
import com.vl.victorlekweuwaweatherapplication.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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

    /*
    This loading variables tells the Splash screen how long it should wait before moving to the
    Home Screen
    */
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    // This is used to time when the Splash Screen should disappear.
    init {
        viewModelScope.launch {
            delay(1000)
            _isLoading.value = false
        }
    }

    /*
    This weather data variable is used to store the weather response from the Openweathermap API
    */
    var _weatherData = MutableLiveData<WeatherResponse?>()
    var weatherData: LiveData<WeatherResponse?> = _weatherData

    /*
    This state variable is a simple way used to update the state of the view model.
    */
    var _state = MutableLiveData<Int>(0)
    var state: LiveData<Int?> = _state

    /*
    This function uses Shared Preferences as the local database to retrieve the name of the last searched city
    */
    fun getLastCitySearched(activity: Activity?) : String {
        val sharedPref = activity?.getSharedPreferences("weather", Context.MODE_PRIVATE)
        val lastCitySearched = sharedPref?.getString(last_city, "") ?: ""
        return lastCitySearched
    }

    /*
    This function uses Shared Preferences as the local database to save the name of the last searched city.
    If the app is closed from the Weather Screen then this name is retrieved to load the weather of
    the last searched city when the app is reopened.
    */
    fun putLastCitySearched(activity: Activity?, lastCity: String) {
        val sharedPref = activity?.getSharedPreferences("weather", Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString(last_city, lastCity)
            apply()
        }
    }

    /*
    This function uses Shared Preferences as the local database to clear the name of the last
    searched city in the event that the user returns to the Home Screen.
    */
    fun clearSharedPreferences(activity: Activity?) {
        val sharedPref = activity?.getSharedPreferences("weather", Context.MODE_PRIVATE)
        sharedPref?.edit()?.remove(last_city)?.commit()
    }

    /*
    This function fetches the weather data from the API for a given city and uses numeric values to
    update the state of the view model.
    */
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
    /*
    This function resets the state of the weather to the initial state.
    The use of this is so that the view model is not in a loading or error state
    when the user navigates back to the Home Screen and the app is reopened.
    */
    fun resetState() {
        _state.value = 0
    }

    /*
    This function gets the name of the user's current city through GeoCoding API and sends the
    city name to the Weather API.
    */
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
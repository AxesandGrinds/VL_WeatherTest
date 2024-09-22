package com.vl.victorlekweuwaweatherapplication.data

import androidx.room.withTransaction
import com.vl.victorlekweuwaweatherapplication.api.WeatherApi
import com.vl.victorlekweuwaweatherapplication.util.networkBoundResource
import kotlinx.coroutines.delay
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val api: WeatherApi,
    private val db: WeatherDatabase
) {

    private val weatherDao = db.weatherDao()

    suspend fun deleteWeather() {
        db.withTransaction {
            weatherDao.deleteWeather()
        }
    }

    fun getWeather(location: String) = networkBoundResource(
        query = {
            weatherDao.getWeather()
        },
        fetch = {
            delay(2000)
            api.getWeatherByLocation(location)
        },
        saveFetchResult = { weather ->
            db.withTransaction {
                weatherDao.deleteWeather()
                weatherDao.insertWeather(weather)
            }
        }
    )

}
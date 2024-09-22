package com.vl.victorlekweuwaweatherapplication.api

import com.vl.victorlekweuwaweatherapplication.api.models.weatherModels.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    companion object {
        const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
        const val API_KEY = "92430d6da15a7c8b938ecef34ad9f329"
    }

    @GET("weather")
    suspend fun getWeatherByLocation(
        @Query("q") location: String,
        @Query("appid") apiKey: String = API_KEY,
    ): WeatherResponse

}
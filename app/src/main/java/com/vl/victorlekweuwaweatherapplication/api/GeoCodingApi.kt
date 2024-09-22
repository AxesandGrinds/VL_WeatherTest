package com.vl.victorlekweuwaweatherapplication.api

import com.vl.victorlekweuwaweatherapplication.api.WeatherApi.Companion
import com.vl.victorlekweuwaweatherapplication.api.models.locationModels.LocationResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoCodingApi {

    companion object {
        const val BASE_URL = "http://api.openweathermap.org/geo/1.0/"
        const val API_KEY = "92430d6da15a7c8b938ecef34ad9f329"
    }

    @GET("reverse")
    suspend fun getCityByLocation(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("limit") limit: Int = 1,
        @Query("appid") apiKey: String = GeoCodingApi.API_KEY,
    ) : ArrayList<LocationResponse>

}
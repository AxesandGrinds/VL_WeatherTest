package com.vl.victorlekweuwaweatherapplication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vl.victorlekweuwaweatherapplication.api.models.weatherModels.WeatherResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Query("SELECT * FROM weather")
    fun getWeather(): Flow<WeatherResponse>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weatherResponse: WeatherResponse)

    @Query("DELETE FROM weather")
    suspend fun deleteWeather()

}
package com.vl.victorlekweuwaweatherapplication.data

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.vl.victorlekweuwaweatherapplication.api.models.weatherModels.Clouds
import com.vl.victorlekweuwaweatherapplication.api.models.weatherModels.Coord
import com.vl.victorlekweuwaweatherapplication.api.models.weatherModels.Main
import com.vl.victorlekweuwaweatherapplication.api.models.weatherModels.Sys
import com.vl.victorlekweuwaweatherapplication.api.models.weatherModels.Weather
import com.vl.victorlekweuwaweatherapplication.api.models.weatherModels.WeatherResponse
import com.vl.victorlekweuwaweatherapplication.api.models.weatherModels.Wind
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.Rule
import org.junit.runner.RunWith
import java.io.IOException

/*
This unit tests test that the Room Database is allowing insertion and deletion of weather data
*/
@RunWith(AndroidJUnit4::class)
class WeatherDatabaseTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var weatherDao: WeatherDao
    private lateinit var db: WeatherDatabase
    private lateinit var weatherData: WeatherResponse

    @Before
    fun setUp() {

        val context = ApplicationProvider.getApplicationContext<Context>()

        // Create a new weather database using Room's in memory database
        db = Room.inMemoryDatabaseBuilder(
            context, WeatherDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        weatherDao = db.weatherDao()

        // Example Weather Response from API Network Call/
        weatherData = WeatherResponse(
            coord= Coord(lon=-73.9236, lat=40.8273),
            weather= arrayListOf(Weather(id=801, main="Clouds", description="few clouds", icon="02n")),
            base="stations",
            main= Main(temp=290.22, feelsLike=289.94, tempMin=288.01, tempMax=291.37,
                pressure=1015, humidity=75, seaLevel=1015, groundLevel=1013), visibility=10000,
            wind= Wind(speed=5.14, deg=20, gust=null),
            clouds= Clouds(all=20), dt=1726983984,
            sys= Sys(type=2, id=57022, country="US", sunrise=1727001818, sunset=1727045573),
            timezone=-14400,
            id=5110253, name="Bronx County", cod=200)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close() // Close room database to delete all entries
    }

    @Test
    fun write_and_read_weather_from_room() = runTest {

        weatherDao.insertWeather(weatherData) // Add the weather data
        weatherDao.getWeather().test{ // Check first emission of weather data
            val emission = awaitItem()  // Get the weather data
            assertEquals(emission.sys?.country, "US") // Check first emission contains needed data
            cancelAndIgnoreRemainingEvents() // Cancel the flow after testing
        }

    }

    @Test
    fun write_and_read_delete_and_weather_from_room() = runTest {

        weatherDao.insertWeather(weatherData) // Add the weather data
        weatherDao.deleteWeather() // Delete the weather data
        weatherDao.getWeather().test{ // Check first emission of weather data
            val emission = awaitItem() // Get the weather data
            assertEquals(emission, null) // Check first emission should not exist
            cancelAndIgnoreRemainingEvents() // Cancel the flow after testing
        }

    }

}
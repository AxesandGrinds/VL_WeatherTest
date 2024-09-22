package com.vl.victorlekweuwaweatherapplication.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vl.victorlekweuwaweatherapplication.api.models.weatherModels.Clouds
import com.vl.victorlekweuwaweatherapplication.api.models.weatherModels.Coord
import com.vl.victorlekweuwaweatherapplication.api.models.weatherModels.Main
import com.vl.victorlekweuwaweatherapplication.api.models.weatherModels.Sys
import com.vl.victorlekweuwaweatherapplication.api.models.weatherModels.Weather
import com.vl.victorlekweuwaweatherapplication.api.models.weatherModels.WeatherResponse
import com.vl.victorlekweuwaweatherapplication.api.models.weatherModels.Wind
import junit.framework.TestCase
import kotlinx.coroutines.flow.collectLatest

import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import kotlinx.coroutines.flow.single
import org.junit.Rule
//import org.mockito.Mock
//import org.mockito.Mockito
//import org.mockito.junit.MockitoJUnitRunner
import org.robolectric.RobolectricTestRunner
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule

/*
I cannot write test cases for the Database and Dao because Hilt documentation is old and the code
it references has been deprecated.
https://developer.android.com/training/dependency-injection/hilt-testing
*/
@RunWith(AndroidJUnit4::class)
class WeatherDatabaseTest : TestCase() {

    private lateinit var weatherDao: WeatherDao
    private lateinit var db: WeatherDatabase

    @Before
    public override fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, WeatherDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        weatherDao = db.weatherDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun writeAndReadWeatherFromRoom() = runTest {

        val weatherData = WeatherResponse(
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

        weatherDao.insertWeather(weatherData)
        weatherDao.getWeather().collectLatest{
            assertEquals(it.sys?.country, "US")
        }

    }

}
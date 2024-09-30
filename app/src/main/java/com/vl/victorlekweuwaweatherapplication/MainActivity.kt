package com.vl.victorlekweuwaweatherapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.vl.victorlekweuwaweatherapplication.api.GeoCodingApi
import com.vl.victorlekweuwaweatherapplication.api.models.weatherModels.WeatherResponse
import com.vl.victorlekweuwaweatherapplication.features.weather.WeatherViewModel
import com.vl.victorlekweuwaweatherapplication.presentation.HomeScreen
import com.vl.victorlekweuwaweatherapplication.presentation.WeatherScreen
import com.vl.victorlekweuwaweatherapplication.ui.theme.VictorLekweuwaWeatherApplicationTheme
import com.vl.victorlekweuwaweatherapplication.util.CustomNavType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable
import javax.inject.Inject
import kotlin.reflect.typeOf

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var geoCodingApi: GeoCodingApi

    val weatherViewModel : WeatherViewModel by viewModels()

    companion object {
        // In order to stop HomeScreen from reloading WeatherScreen every time the composable recomposes,
        // this static variable is use to track when exactly the Weather screen is allowed to reload.
        var freshScreenCity = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Showing the splash screen for 1 second before showing this activity
        installSplashScreen().apply {
            setKeepOnScreenCondition() {
                weatherViewModel.isLoading.value
            }
        }

        // Enables transparent system bar style
        enableEdgeToEdge()

        setContent {
            VictorLekweuwaWeatherApplicationTheme {
                val navController = rememberNavController()
                Navigation(navController, geoCodingApi, weatherViewModel)
            }
        }

    }

}

@Serializable
data object HomeRoute

@Serializable
data class WeatherRoute(
    val city: String,
    val weatherData: WeatherResponse
)

// This class holds the nav graph for the application which are just two composable functions
@Composable
fun Navigation(navController: NavHostController, geoCodingApi: GeoCodingApi, weatherViewModel: WeatherViewModel) {

    NavHost(navController = navController, startDestination = HomeRoute) {

        // This composable is the Home Screen which allows the user to put in a city to get the weather.
        composable<HomeRoute> {
            HomeScreen(
                weatherViewModel,
                geoCodingApi,
                onSearch = { city, weatherData ->
                    navController.navigate(
                        WeatherRoute(city, weatherData)
                    )
                },
            )
        }

        // This composable is the Weather Screen which displays the weather data for the given city.
        composable<WeatherRoute>(
            typeMap = mapOf(
                typeOf<WeatherResponse>() to CustomNavType.WeatherResponse
            )
        ){
            val arguments = it.toRoute<WeatherRoute>()
            WeatherScreen(arguments.city, arguments.weatherData, weatherViewModel, navController)
        }

    }

}

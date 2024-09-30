package com.vl.victorlekweuwaweatherapplication.presentation

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import coil.compose.AsyncImage
import coil.imageLoader
import coil.util.DebugLogger
import com.vl.victorlekweuwaweatherapplication.HomeRoute
import com.vl.victorlekweuwaweatherapplication.MainActivity
import com.vl.victorlekweuwaweatherapplication.api.models.weatherModels.WeatherResponse
import com.vl.victorlekweuwaweatherapplication.features.weather.WeatherViewModel

/*
This screen, the Weather Screen. is the screen the user sees after entering the name of the city and
clicking the search button from the Home Screen. This screen displays pertinent weather information
of the city the user entered. This screen received the weather data variable which contains the weather
data for the city from the network call. This screen also takes in the shared view model only for the
purpose of resetting the state of the app so if the user navigates back then the app does not automatically
again navigate back to this screen.
*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    city: String,
    weatherData: WeatherResponse, // Passing the weather data so not to recompose when state is updated from the view model
    weatherViewModel: WeatherViewModel,
    navController: NavController
) {

    /*
    The numeric state of the view model is reset only once so that recomposition does not cause
    subsequent resets.
    */
    LaunchedEffect(Unit) {

        weatherViewModel.resetState()

    }

    val context = LocalContext.current
    val activity = context as? Activity

    // Put the last location the user queried in shared preferences.
    weatherViewModel.putLastCitySearched(activity, city)

    println("ATTENTION ATTENTION: State should be 0: Weather View Model State: ${weatherViewModel.state.value}")

    // The weather icon is in the response model. Simply need to add ".png".
    val weatherIcon = weatherData.weather?.get(0)?.icon ?: "01d"

    // If the weather response is null for some reason then show some arbitrary information.
    val weatherIconDescription = weatherData.weather?.get(0)?.description ?: "Might be sunny"

    // This string stores the network image of the current weather status in the Weather Response.
    val weatherIconAddress = "http://openweathermap.org/img/w/${weatherIcon}.png"

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val imageLoader = LocalContext.current.imageLoader.newBuilder()
        .logger(DebugLogger())
        .build()

    println("ATTENTION ATTENTION: Weather Icon Address ${weatherIconAddress}")

    /*
    This intercepts the back press and pops the backstack back to the Home Screen.
    It also clears any shared preferences and resets the state of the weather view model.
    This helps the app not automatically load the weather of the last searched
    city after clicking the back button.
    MainActivity.freshScreenCity further provides a check to see if the app should
    try to reload the weather of the last searched city.
    */
    BackHandler(true) {
        weatherViewModel.clearSharedPreferences(activity)
        weatherViewModel.putLastCitySearched(activity, "null")
        weatherViewModel.resetState()
        MainActivity.freshScreenCity = false
        navController.navigate(HomeRoute) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = false
        }
    }

    Scaffold(

        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),

        topBar = {

            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                ),
                title = {
                    Text(
                        "Weather App - $city",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp),
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        /*
                        Clear shared preferences and reset the state of the weather view model.
                        This helps the app not automatically load the weather of the last searched
                        city after clicking the back button.
                        MainActivity.freshScreenCity further provides a check to see if the app should
                        try to reload the weather of the last searched city.
                        */
                        MainActivity.freshScreenCity = false
                        weatherViewModel.clearSharedPreferences(activity)
                        weatherViewModel.putLastCitySearched(activity, "null")
                        weatherViewModel.resetState()
                        navController.navigate(HomeRoute) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = false
                        }

                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back Button",
                            tint = Color.Black
                        )
                    }
                }

            )
        },
        content = { innerPadding ->

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(Color.LightGray)
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {

                ElevatedCard(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White,
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(70.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(5.dp)
                    ) {
                        AsyncImage(
                            model = weatherIconAddress,
                            imageLoader = imageLoader,
                            contentDescription = "Image of ${weatherIconDescription} weather",
                            modifier = Modifier.size(180.dp)
                        )

                        SpacerH()

                        WeatherInfoText("Country Code: ${weatherData.sys?.country ?: "Unknown"}")

                        SpacerH()

                        WeatherInfoText("Location: ${weatherData.name ?: "Unknown"}")

                        SpacerH()

                        WeatherInfoText("Weather: ${weatherData.weather?.get(0)?.description?.toSentenceCase() ?: "Unknown"}")

                        SpacerH()

                        WeatherInfoText("Latitude: ${weatherData.coord.lat ?: "0"}")

                        SpacerH()

                        WeatherInfoText("Longitude: ${weatherData.coord.lon ?: "0"}")

                        SpacerH()

                        WeatherInfoText("Wind Speed: ${weatherData.wind?.speed ?: "Unknown"} m/s")

                        SpacerH()

                        WeatherInfoText("Temperature: ${weatherData.main?.temp ?: "Unknown"}°C")

                        SpacerH()

                        WeatherInfoText("Pressure: ${weatherData.main?.pressure ?: "Unknown"} Pa")

                        SpacerH()

                        WeatherInfoText("Humidity: ${weatherData.main?.humidity ?: "Unknown"} g/m\u00B3")

                        SpacerH()

                    }

                }

            }

        }

    )

}

// Simple Spacer with the same height allows to set equal spacing once for in between all texts.
@Composable
fun SpacerH() {
    Spacer(modifier = Modifier.height(16.dp))
}

// Simple Text composable with same font size and color for each string of information.
@Composable
fun WeatherInfoText(infoString: String) {
    Text(
        infoString,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis,
        style = TextStyle(
            fontSize = 16.sp,
            color = Color.Black
        )
    )
}

//vCapitalize the first character of each word in the string.
fun String.toSentenceCase(): String {
    return this.split(" ")
        .joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { it.titlecaseChar() }
        }
}
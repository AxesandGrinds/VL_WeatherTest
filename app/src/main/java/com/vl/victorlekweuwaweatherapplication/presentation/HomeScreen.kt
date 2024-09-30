package com.vl.victorlekweuwaweatherapplication.presentation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.vl.victorlekweuwaweatherapplication.MainActivity
import com.vl.victorlekweuwaweatherapplication.api.GeoCodingApi
import com.vl.victorlekweuwaweatherapplication.api.models.weatherModels.WeatherResponse
import com.vl.victorlekweuwaweatherapplication.features.weather.WeatherViewModel
import kotlinx.coroutines.launch

/*
This screen, the Home Screen. is the first screen the user sees after the splash screen.
If the user enters a city name and is sent to the Weather Screen, the the app remembers the last city
the user visited and returns to that screen if the user had previously terminated the app from the
Weather Screen.
*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(weatherViewModel: WeatherViewModel, geoCodingApi: GeoCodingApi, onSearch: (String, WeatherResponse) -> Unit) {

    val context = LocalContext.current
    val activity = context as? Activity

    val state = weatherViewModel.state.observeAsState().value

    /*
    This value holds the text the user entered for the city weather search.
    and is held by the text field and can be cleared once the user clicks on the done button in
    the virtual keyboard.
    */
    val cityText = remember {
        mutableStateOf("")
    }

    // This value is sent t to a network call and the subsequent Weather Screen.
    val chosenCityText = remember {
        mutableStateOf("")
    }

    // Check if the user recently searched for a city and exited the app right after
    // before going back to the home screen.
    val lastCitySearched = weatherViewModel.getLastCitySearched(activity)
    if (
        lastCitySearched.isNotEmpty() &&
        lastCitySearched.isNotBlank() &&
        lastCitySearched != "null" &&
        MainActivity.freshScreenCity
        ) {
        weatherViewModel.getWeather(lastCitySearched)
    }

    // We create a keyboard controller so that we can hide the keyboard when the user
    // clicks on the done button on the keyboard.
    val keyboardController = LocalSoftwareKeyboardController.current

    // We show a snack bar once only when the user denies runtime location permission
    val snackbarHostState = remember { SnackbarHostState() }

    val coroutineScope = rememberCoroutineScope()

    // The user may request for their current location to be accessed in determining the day's weather.
    // The following function checks if Location is enabled using the context provided from the activity.
    fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    // If the user accepts the runtime location permission, we get the user's location via the GeoCoding API.
    // Check Location permission and if granted, go to the Weather Screen with the user's current location.
    fun getLastLocation() {
        if (isLocationEnabled()) {
            val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            if (activity != null) {
                fusedLocationClient.lastLocation.addOnCompleteListener(activity) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        //use the location latitude and longitude as per your use.
                        val latitude = location.latitude
                        val longitude = location.longitude

                        weatherViewModel.getCityFromLatLon(latitude, longitude, geoCodingApi)
                    }
                }
            }
        }

    }

    // If the user grants the location permission then use the user's last known location to get the location
    // of the user. Otherwise, if the user denied the location permission, then guide the user on hoe
    // to enable location in the device settings.
    val locationPermissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
                getLastLocation()
            }
            permissions.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                getLastLocation()
            }
            else -> {
                // No location access granted. Show snackbar explaining what to do.
                coroutineScope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = "Go to your device's settings to provide location to this app to use this feature.",
                        actionLabel = "Ok",
                        duration = SnackbarDuration.Long
                    )
                    when (result) {
                        SnackbarResult.ActionPerformed -> {
                            /* Handle snackbar action performed */
                            snackbarHostState.currentSnackbarData?.dismiss()
                        }
                        SnackbarResult.Dismissed -> {
                            /* Handle snackbar dismissed */
                        }
                    }
                }
            }
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(

        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },

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
                        "Weather App",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp),
                        overflow = TextOverflow.Ellipsis
                    )
                },

                )
        },
        content = { innerPadding ->

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(Color(0xff1e88e5))
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
                        .padding(innerPadding)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(5.dp)
                    ) {
                        OutlinedTextField(
                            value = cityText.value,
                            onValueChange = {
                                cityText.value = it
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction = ImeAction.Done,
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {keyboardController?.hide()}
                            ),
                            modifier = Modifier
                                .padding(16.dp),
                            label = {
                                Text(
                                    "Enter City Name",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                        .wrapContentHeight(Alignment.CenterVertically),
                                    textAlign = TextAlign.Center,
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Blue,
                                    )
                                )
                            },

                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedButton(onClick = {

                            // If the user has entered a city name and clicked submit then clear the
                            // outline text field and navigate to that location using the Weather Screen
                            // After clearing the outline text field then save the city name to chosenCityText
                            // which would later be used to navigate to the Weather Screen.
                            if (cityText.value.isNotBlank()) {
                                keyboardController?.hide()
                                chosenCityText.value = cityText.value
                                cityText.value = ""

                                weatherViewModel.getWeather(chosenCityText.value)

                            }

                        }) {

                            Text(
                                "Search",
                                style = TextStyle(fontSize = 20.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Blue)
                            )

                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        /*
                        When the user clicks on this button then the app gets their current longitude and latitude
                        The locationPermissionRequest then request navigates to the Weather Screen if
                        the user entered an accurate city name.
                        */
                        OutlinedButton(onClick = {

                            locationPermissionRequest.launch(
                                arrayOf(
                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            )

                        }) {

                            Text(
                                "Use My Current Location",
                                style = TextStyle(fontSize = 20.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Blue)
                            )

                        }

                        Spacer(modifier = Modifier.height(20.dp))

                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                /*
                The view model provide numeric values depicting the state of the app.
                State 1 - Initial state - Nothing needs to be done.
                State 2 - Loading State - The view model is currently doing a network call.
                State 3 - Error State - Show an error message.
                State 4 - Weather Data Successful State - The user entered an accurate city name and is about to be navigated
                             to the Weather Screen.
                */
                when (state) {

                    0 -> {
                        // Initialized State 0 - Do Nothing
                    }
                    1 -> {
                        // Loading State 1 - Progress Dialog Circling
                        CircularProgressIndicator(
                            modifier = Modifier.width(64.dp),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    }
                    2 -> {
                        // Error State 2 -Show Error
                        Text(
                            "City Not Recognized. Please Try Again.",
                            style = TextStyle(fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Red)
                        )
                    }
                    3 -> {
                        /*
                         Weather Data Successful State 3 - Navigate
                         This code is put in the launched effect so that it does not navigate to the
                         Weather Screen numerous times which may sometimes happens.
                        */
                        LaunchedEffect(Unit) {
                            println("ATTENTION ATTENTION: STATE 3 - NAVIGATING TO Weather Screen")
                            /*
                            When the user uses their current location (longitude and latitude) then
                            city name needs to be populated from the weather response rather than
                            the text entered into the text field.
                            */
                            val cityName = if (chosenCityText.value.isEmpty() || chosenCityText.value.isBlank()) {
                                weatherViewModel.weatherData.value?.name
                            }
                            else {
                                chosenCityText.value
                            }
                            onSearch(cityName!!, weatherViewModel.weatherData.value!!)
                        }
                    }

                }

            }

        })

}
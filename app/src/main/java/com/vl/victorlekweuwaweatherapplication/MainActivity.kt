package com.vl.victorlekweuwaweatherapplication

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil.compose.AsyncImage
import coil.imageLoader
import coil.util.DebugLogger
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.vl.victorlekweuwaweatherapplication.api.GeoCodingApi
import com.vl.victorlekweuwaweatherapplication.api.models.weatherModels.WeatherResponse
import com.vl.victorlekweuwaweatherapplication.features.weather.WeatherViewModel
import com.vl.victorlekweuwaweatherapplication.ui.theme.VictorLekweuwaWeatherApplicationTheme
import com.vl.victorlekweuwaweatherapplication.util.CustomNavType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import javax.inject.Inject
import kotlin.reflect.typeOf

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var geoCodingApi: GeoCodingApi

    val weatherViewModel : WeatherViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Showing the splash screen for 1 second
        installSplashScreen().apply {
            setKeepOnScreenCondition() {
                weatherViewModel.isLoading.value
            }
        }

        enableEdgeToEdge()

        setContent {

            VictorLekweuwaWeatherApplicationTheme {
                val navController = rememberNavController()
                Navigation(navController, geoCodingApi, weatherViewModel)
            }
        }

    }

}

sealed class Screen(val route: String) {

    object Home: Screen("home")
    object Weather: Screen("weather")

    fun withArgs(vararg args: String) : String {

        return buildString {
            append(route)
            args.forEach { arg ->
                append("/{arg}")
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

var freshScreenCity = true

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(weatherViewModel: WeatherViewModel, geoCodingApi: GeoCodingApi, onSearch: (String, WeatherResponse) -> Unit) {

    val context = LocalContext.current
    val activity = context as? Activity

    val state = weatherViewModel.state.observeAsState().value

    val cityText = remember {
        mutableStateOf("")
    }

    val chosenCityText = remember {
        mutableStateOf("")
    }

    // Check if the user recently searched for a city and exited the app right after
    // before going back to the home screen.
    val lastCitySearched = weatherViewModel.getLastCitySearched(activity)
    if (lastCitySearched.isNotBlank() && freshScreenCity) {
        weatherViewModel.getWeather(lastCitySearched)
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Check if Location is enabled
    fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    // Check Location permission and if granted, go to the user's current location
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
                // No location access granted.
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Go to settings to provide location to this app.",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(

        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),

        topBar = {

            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Magenta,
                    titleContentColor = Color.Black
                ),
                title = {
                    Text(
                        "Weather App",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp),
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
                    ) {
                        OutlinedTextField(
                            value = cityText.value,
                            onValueChange = {
                                cityText.value = it
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done,
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {keyboardController?.hide()}
                            ),
                            label = {
                                Text(
                                    "Enter City Name",
                                    style = TextStyle(fontSize = 30.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Blue)
                                )
                            },
                            modifier = Modifier.padding(16.dp)
                        )

                        OutlinedButton(onClick = {

                            if (cityText.value.isNotBlank()) {
                                keyboardController?.hide()
                                chosenCityText.value = cityText.value
                                cityText.value = ""

                                weatherViewModel.getWeather(chosenCityText.value)

                            }

                        }) {

                            Text(
                                "Search",
                                style = TextStyle(fontSize = 25.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Blue)
                            )

                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedButton(onClick = {

                            locationPermissionRequest.launch(
                                arrayOf(
                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            )

                        }) {

                            Text(
                                "Use My Current Location",
                                style = TextStyle(fontSize = 25.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Blue)
                            )

                        }

                        Spacer(modifier = Modifier.height(20.dp))

                    }
                }


                Spacer(modifier = Modifier.height(20.dp))

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
                        // Weather Data Successful State 3 - Navigate
                        LaunchedEffect(Unit) {
                            println("ATTENTION ATTENTION: STATE 3 - NAVIGATING TO Weather Screen")
                            onSearch(chosenCityText.value, weatherViewModel.weatherData.value!!)
                        }
                    }

                }

            }

        })

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    city: String,
    weatherData: WeatherResponse, // Passing the weather data so not to recompose when state is updated from the view model
    weatherViewModel: WeatherViewModel,
    navController: NavController
) {

    LaunchedEffect(Unit) {

        weatherViewModel.resetState()

    }

    val context = LocalContext.current
    val activity = context as? Activity

    weatherViewModel.putLastCitySearched(activity, city)

    println("ATTENTION ATTENTION: State should be 0: Weather View Model State: ${weatherViewModel.state.value}")

    val weatherIcon = weatherData.weather?.get(0)?.icon ?: "01d"
    val weatherIconDescription = weatherData.weather?.get(0)?.description ?: "Might be sunny"
    val weatherIconAddress = "http://openweathermap.org/img/w/${weatherIcon}.png"

    val textColor = Color.Black
    val spacerHeight = 20.dp

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val imageLoader = LocalContext.current.imageLoader.newBuilder()
        .logger(DebugLogger())
        .build()

    println("ATTENTION ATTENTION: Weather Icon Address ${weatherIconAddress}")

    BackHandler(true) {
        weatherViewModel.clearSharedPreferences(activity)
        weatherViewModel.resetState()
        freshScreenCity = false
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
                    containerColor = Color.Magenta,
                    titleContentColor = Color.Black
                ),
                title = {
                    Text(
                        "Weather App",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp),
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        weatherViewModel.clearSharedPreferences(activity)
                        weatherViewModel.resetState()
                        freshScreenCity = false
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
                    .background(Color.Yellow)
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
                    ) {
                        AsyncImage(
                            model = weatherIconAddress,
                            imageLoader = imageLoader,
                            contentDescription = "Image of ${weatherIconDescription} weather",
                            modifier = Modifier.size(200.dp)
                        )

                        Spacer(modifier = Modifier.height(spacerHeight))

                        Text("Country Code: ${weatherData.sys?.country ?: "Unknown"}",
                            style = TextStyle(fontSize = 25.sp,
                                color = textColor)
                        )

                        Spacer(modifier = Modifier.height(spacerHeight))

                        Text("Longitude: ${weatherData.coord?.lon ?: "0"}",
                            style = TextStyle(fontSize = 25.sp,
                                color = textColor)
                        )

                        Spacer(modifier = Modifier.height(spacerHeight))

                        Text("Latitude: ${weatherData.coord?.lat ?: "0"}",
                            style = TextStyle(fontSize = 25.sp,
                                color = textColor)
                        )

                        Spacer(modifier = Modifier.height(spacerHeight))

                        Text("Location: ${weatherData.name ?: "Unknown"}",
                            style = TextStyle(fontSize = 25.sp,
                                color = textColor)
                        )

                        Spacer(modifier = Modifier.height(spacerHeight))

                        Text("Wind Speed: ${weatherData.wind?.speed ?: "Unknown"}",
                            style = TextStyle(fontSize = 25.sp,
                                color = textColor)
                        )

                        Spacer(modifier = Modifier.height(spacerHeight))

                        Text("Temperature: ${weatherData.main?.temp ?: "Unknown"}°",
                            style = TextStyle(fontSize = 25.sp,
                                color = textColor)
                        )

                        Spacer(modifier = Modifier.height(spacerHeight))

                        Text("Pressure: ${weatherData.main?.pressure ?: "Unknown"}",
                            style = TextStyle(fontSize = 25.sp,
                                color = textColor)
                        )

                        Spacer(modifier = Modifier.height(spacerHeight))

                        Text("Humidity: ${weatherData.main?.humidity ?: "Unknown"}",
                            style = TextStyle(fontSize = 25.sp,
                                color = textColor)
                        )

                        Spacer(modifier = Modifier.height(spacerHeight))

                    }

                }

            }

        }

    )

}

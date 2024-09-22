package com.vl.victorlekweuwaweatherapplication.util

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.vl.victorlekweuwaweatherapplication.api.models.weatherModels.WeatherResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object CustomNavType {
    val WeatherResponse = object : NavType<WeatherResponse>(
        isNullableAllowed = false
    ) {

        override fun serializeAsValue(value: WeatherResponse): String {
            return Uri.encode(Json.encodeToString(value))
        }

        override fun get(bundle: Bundle, key: String): WeatherResponse? {
            return Json.decodeFromString(bundle.getString(key) ?: return null)
        }

        override fun parseValue(value: String): WeatherResponse {
            return Json.decodeFromString(Uri.decode(value))
        }

        override fun put(bundle: Bundle, key: String, value: WeatherResponse) {
            bundle.putString(key, Json.encodeToString(value))
        }

    }
}
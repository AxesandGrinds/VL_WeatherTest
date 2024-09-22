package com.vl.victorlekweuwaweatherapplication.util.typeConverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vl.victorlekweuwaweatherapplication.api.models.weatherModels.Weather

class WeatherTypeConverter {

    @TypeConverter
    fun fromWeather(weathers: ArrayList<Weather?>) : String {
        val gson = Gson()
        val jsonString = gson.toJson(weathers)
        println(jsonString)
        return jsonString
    }

    @TypeConverter
    fun toWeather(weathersString: String) : ArrayList<Weather?> {
        val type = object : TypeToken<ArrayList<Weather>>() {}.type
        val gson = Gson()
        val weatherFromJson = gson.fromJson<ArrayList<Weather?>>(weathersString, type)
        println(weatherFromJson)
        return weatherFromJson
    }

}
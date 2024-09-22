package com.vl.victorlekweuwaweatherapplication.util.typeConverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.vl.victorlekweuwaweatherapplication.api.models.weatherModels.Clouds

class CloudsTypeConverter {

    @TypeConverter
    fun fromClouds(clouds: Clouds) : String {
        val gson = Gson()
        val jsonString = gson.toJson(clouds)
        println(jsonString)
        return jsonString
    }

    @TypeConverter
    fun toClouds(cloudsString: String) : Clouds {
        val gson = Gson()
        val cloudsFromJson = gson.fromJson(cloudsString, Clouds::class.java)
        println(cloudsFromJson)
        return cloudsFromJson
    }

}
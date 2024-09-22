package com.vl.victorlekweuwaweatherapplication.util.typeConverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.vl.victorlekweuwaweatherapplication.api.models.weatherModels.Wind

class WindTypeConverter {

    @TypeConverter
    fun fromWind(wind: Wind) : String {
        val gson = Gson()
        val jsonString = gson.toJson(wind)
        println(jsonString)
        return jsonString
    }

    @TypeConverter
    fun toWind(windString: String) : Wind {
        val gson = Gson()
        val windFromJson = gson.fromJson(windString, Wind::class.java)
        println(windFromJson)
        return windFromJson
    }

}
package com.vl.victorlekweuwaweatherapplication.util.typeConverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.vl.victorlekweuwaweatherapplication.api.models.weatherModels.Main

class MainTypeConverter {

    @TypeConverter
    fun fromMain(main: Main) : String {
        val gson = Gson()
        val jsonString = gson.toJson(main)
        println(jsonString)
        return jsonString
    }

    @TypeConverter
    fun toMain(mainString: String) : Main {
        val gson = Gson()
        val mainFromJson = gson.fromJson(mainString, Main::class.java)
        println(mainFromJson)
        return mainFromJson
    }

}
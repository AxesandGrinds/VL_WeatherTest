package com.vl.victorlekweuwaweatherapplication.util.typeConverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.vl.victorlekweuwaweatherapplication.api.models.weatherModels.Sys

class SysTypeConverter {

    @TypeConverter
    fun fromSys(sys: Sys) : String {
        val gson = Gson()
        val jsonString = gson.toJson(sys)
        println(jsonString)
        return jsonString
    }

    @TypeConverter
    fun toSys(sysString: String) : Sys {
        val gson = Gson()
        val sysFromJson = gson.fromJson(sysString, Sys::class.java)
        println(sysFromJson)
        return sysFromJson
    }

}
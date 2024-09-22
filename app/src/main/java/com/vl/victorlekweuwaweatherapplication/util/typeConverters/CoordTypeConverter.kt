package com.vl.victorlekweuwaweatherapplication.util.typeConverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.vl.victorlekweuwaweatherapplication.api.models.weatherModels.Coord

class CoordTypeConverter {

    @TypeConverter
    fun fromCoord(coord: Coord) : String {
        val gson = Gson()
        val jsonString = gson.toJson(coord)
        println(jsonString)
        return jsonString
    }

    @TypeConverter
    fun toCoord(coordString: String) : Coord {
        val gson = Gson()
        val coordFromJson = gson.fromJson(coordString, Coord::class.java)
        println(coordFromJson)
        return coordFromJson
    }

}
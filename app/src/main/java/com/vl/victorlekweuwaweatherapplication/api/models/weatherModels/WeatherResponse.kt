package com.vl.victorlekweuwaweatherapplication.api.models.weatherModels

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
@Entity("weather")
data class WeatherResponse (

    @PrimaryKey
    @SerializedName("coord")
    val coord : Coord,

    @SerializedName("weather")
    val weather : ArrayList<Weather?>?,

    @SerializedName("base")
    val base : String?,

    @SerializedName("main")
    val main : Main?,

    @SerializedName("visibility")
    val visibility : Int?,

    @SerializedName("wind")
    val wind : Wind?,

    @SerializedName("clouds")
    val clouds : Clouds?,

    @SerializedName("dt")
    val dt : Int?,

    @SerializedName("sys")
    val sys : Sys? = Sys(),

    @SerializedName("timezone")
    val timezone : Int?,

    @SerializedName("id")
    val id : Int?,

    @SerializedName("name")
    val name : String?,

    @SerializedName("cod")
    val cod : Int?

)
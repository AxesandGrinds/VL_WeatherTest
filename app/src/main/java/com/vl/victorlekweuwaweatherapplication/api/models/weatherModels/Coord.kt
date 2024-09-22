package com.vl.victorlekweuwaweatherapplication.api.models.weatherModels

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Coord (

    @SerializedName("lon") var lon : Double? = null,
    @SerializedName("lat") var lat : Double? = null

)
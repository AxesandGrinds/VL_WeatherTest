package com.vl.victorlekweuwaweatherapplication.api.models.weatherModels

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Clouds (

    @SerializedName("all") var all : Int? = null

)
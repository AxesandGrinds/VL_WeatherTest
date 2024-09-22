package com.vl.victorlekweuwaweatherapplication.api.models.weatherModels

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Sys (

    @SerializedName("type")
    var type : Int? = null,

    @SerializedName("id")
    var id : Int? = null,

    @SerializedName("country")
    var country : String? = null,

    @SerializedName("sunrise")
    var sunrise : Int? = null,

    @SerializedName("sunset")
    var sunset  : Int? = null

)
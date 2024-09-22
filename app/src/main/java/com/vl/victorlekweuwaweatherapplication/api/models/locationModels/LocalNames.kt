package com.vl.victorlekweuwaweatherapplication.api.models.locationModels

import com.google.gson.annotations.SerializedName

data class LocalNames (

    @SerializedName("he")
    var he : String? = null,

    @SerializedName("en")
    var en : String? = null,

    @SerializedName("mk")
    var mk : String? = null,

    @SerializedName("be")
    var be : String? = null,

    @SerializedName("ru")
    var ru : String? = null,

    @SerializedName("fr")
    var fr : String? = null,

    @SerializedName("cy")
    var cy : String? = null,

    @SerializedName("ko")
    var ko : String? = null

)
package com.example.mystories.api

import com.google.gson.annotations.SerializedName

data class UserResponse(

    @SerializedName("error")
    var error: Boolean? = false,

    @SerializedName("message")
    val message: String? = null,

)
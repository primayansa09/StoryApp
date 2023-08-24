package com.example.mystories.api

import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@SerializedName("loginResult")
	val loginResult: LoginResult? = null,

	@SerializedName("error")
	val error: Boolean? = null,

	@SerializedName("message")
	val message: String? = null
)

data class LoginResult(

	@SerializedName("name")
	val name: String? = null,

	@SerializedName("userId")
	val userId: String? = null,

	@SerializedName("token")
	val token: String? = null
)

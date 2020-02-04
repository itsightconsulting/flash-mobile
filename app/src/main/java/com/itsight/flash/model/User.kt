package com.itsight.flash.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int?,
    val name: String,
    @SerializedName("last_name")
    val lastName: String,
    val dni: String,
    val email: String,
    val username: String,
    val password: String){

    constructor(name: String, lastName: String, dni: String, email: String, username: String, password: String)
            :this(0, name, lastName, dni, email, username, password)
}
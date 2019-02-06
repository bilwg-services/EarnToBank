package com.deucate.earntobank.auth

import java.io.Serializable

data class User(
    val Name:String,
    val Email:String,
    val ImageURL:String,
    val UID:String
):Serializable
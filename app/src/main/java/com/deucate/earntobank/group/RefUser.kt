package com.deucate.earntobank.group

import com.google.firebase.Timestamp

data class RefUser(
    val Name: String,
    val Time: Timestamp,
    val ImageURL: String,
    val uid: String
)
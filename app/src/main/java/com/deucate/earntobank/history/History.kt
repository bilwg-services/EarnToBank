package com.deucate.earntobank.history

data class History(
    val id: String,
    val amount: Long,
    val mobileNumber: String,
    val status: Boolean
)
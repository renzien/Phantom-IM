package com.renzien.phantomim.data.model

import com.google.firebase.Timestamp

data class UserProfile(
    val uid: String,
    val username: String,
    val usernameKey: String,
    val createdAt: Timestamp? = null
)
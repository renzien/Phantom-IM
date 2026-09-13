package com.renzien.phantomim.data.model

import com.google.firebase.firestore.DocumentSnapshot

data class FriendsPage(
    val profiles: List<UserProfile>,
    val nextCursor: DocumentSnapshot? = null
)